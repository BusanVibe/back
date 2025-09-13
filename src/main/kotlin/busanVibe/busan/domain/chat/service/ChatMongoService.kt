package busanVibe.busan.domain.chat.service

import busanVibe.busan.domain.chat.domain.ChatMessage
import busanVibe.busan.domain.chat.dto.websocket.ChatMessageReceiveDTO
import busanVibe.busan.domain.chat.dto.websocket.ChatMessageResponseDTO
import busanVibe.busan.domain.chat.dto.websocket.ChatMessageSendDTO
import busanVibe.busan.domain.chat.enums.MessageType
import busanVibe.busan.domain.chat.repository.ChatMongoRepository
import busanVibe.busan.domain.user.data.User
import busanVibe.busan.domain.user.repository.UserRepository
import busanVibe.busan.domain.user.service.login.AuthService
import busanVibe.busan.global.apiPayload.code.status.ErrorStatus
import busanVibe.busan.global.apiPayload.exception.GeneralException
import busanVibe.busan.global.apiPayload.exception.handler.ExceptionHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ChatMongoService(
    private val chatMongoRepository: ChatMongoRepository,
    private val redisPublisher: RedisPublisher,
    private val topic: ChannelTopic,
    private val userRepository: UserRepository,
    private val openAiService: OpenAiService,
    @Value("\${image.chat-bot}")
    private val chatBotImage: String,
    @Value("\${image.guest}")
    private val guestImage: String,
) {

    val log: Logger = LoggerFactory.getLogger(ChatMongoService::class.java)

    /**
     *
     * 일반 채팅의 경우
     * 메시지 전송 - 메시지를 DB에 저장 - dto 생성 - dto를 웹소켓 전송 - 클라이언트 갱신 - dto를 반환(응답)
     *
     * 챗봇의 경우
     * 메시지 전송 - openai 요청 후 메시지 받음 - dto 생성 - dto를 반환(응답)
     *
     */
    fun saveAndPublish(chatMessage: ChatMessageSendDTO): ChatMessageReceiveDTO {

        // 필요한 정보 미리 정의
        val currentUser = AuthService().getCurrentUser()
        val now = LocalDateTime.now()
        val message = chatMessage.message.trim()

        // 받은 메세지로 타입 구분
        // 일반 메시지는 CHAT, 챗봇 요청은 BOT_REQUEST
        val type: MessageType = if(message[0] == '/'){
            MessageType.BOT_REQUEST
        }else{
            MessageType.CHAT
        }

        // 채팅 로그 생성
        log.info("[$type] userId: ${currentUser.id}, /POST/chat/send, 메시지 전송: $chatMessage")

        // ChatMessage 객체 생성
        val chat = ChatMessage(
            type = type,
            userId = currentUser.id,
            message = message,
            time = now
        )

        // 사용자가 보낸 메시지 저장
        chatMongoRepository.save(chat)

        // 유저들에게 웹소켓으로 전달할 메시지의 DTO 생성 - CHAT
        val receiveDto = buildReceiveDto(type, currentUser, chat, now)

        // 일반 채팅일 경우에만 유저들에게 웹소켓 메시지 보냄
        if(receiveDto.type == MessageType.CHAT) {
            redisPublisher.publish(topic, receiveDto)
        }

        // OpenAI 챗봇 요청
        if (receiveDto.type == MessageType.BOT_RESPONSE) {
            // openai 요청
            openAiService.chatToOpenAI(chat.message)
            // 챗봇 대답 저장
            chatMongoRepository.save(
                ChatMessage(
                    type = MessageType.BOT_RESPONSE,
                    userId = currentUser.id,
                    message = receiveDto.message,
                    time = chat.time
                )
            )
        }

        return receiveDto
    }

    // ChatMessageReceiveDTO 생성
    private fun buildReceiveDto(
        type: MessageType,
        currentUser: User,
        chat: ChatMessage,
        timestamp: LocalDateTime
    ): ChatMessageReceiveDTO {
        return if (type == MessageType.CHAT) {
            ChatMessageReceiveDTO(
                name = currentUser.nickname,
                imageUrl = currentUser.profileImageUrl,
                message = chat.message,
                time = timestamp,
                type = type,
                userId = currentUser.id
            )
        } else {
            val message = openAiService.chatToOpenAI(chat.message)
            ChatMessageReceiveDTO(
                name = "챗봇",
                imageUrl = chatBotImage,
                message = message,
                time = timestamp,
                type = MessageType.BOT_RESPONSE,
                userId = currentUser.id
            )
        }
    }

    fun getChatHistory(cursorId: String?, pageSize: Int = 15): ChatMessageResponseDTO.ListDto {

        // 요청값 검증
        // pageSize
        if(pageSize < 0){
            throw GeneralException(ErrorStatus.INVALID_PAGE_SIZE_MINUS)
        }else if (pageSize > 30) {
            throw GeneralException(ErrorStatus.INVALID_PAGE_SIZE_BIG)
        }

        // 현재 로그인한 유저 조회
        val currentUser = AuthService().getCurrentUser()
        val currentUserId: Long = currentUser.id ?: throw ExceptionHandler(ErrorStatus.USER_NOT_FOUND)

        // Pageable 객체 생성
        val pageable = PageRequest.of(0, pageSize, Sort.by(Sort.Direction.DESC, "time"))

        // 조회 -> List<ChatMessage> 변수 선언 및 초기화
        // 채팅 기록 조회
        val chatHistory: List<ChatMessage> = if (cursorId.isNullOrBlank() || cursorId == "null") {
            // cursorId가 없으면: 최신 메시지 조회 (처음 불러올 때)
            chatMongoRepository.findAllWithBotFilter(currentUserId, pageable)
        } else {
            // cursorId가 있으면: 이전 메시지 이어서 조회 (cursorId보다 작은 메시지)
            chatMongoRepository.findByIdLessThanWithBotFilter(currentUserId, cursorId, pageable)
        }


        // userId List 저장. 바로 뒤에 userId로 User 정보들을 먼저 찾고, 그 뒤에 DTO 변환
        val userIdList: List<Long> = chatHistory.mapNotNull { it -> it.userId }.toList()

        // <userId, User> -> Map<Long, User>로 저장
        val userMap: Map<Long, User> = userRepository.findUsersByIdIn(userIdList).associateBy { it.id as Long }

        // DTO 변환
        val dtoList = chatHistory.map { chat ->
            val user: User? = userMap[chat.userId ?: -1]
            val profileImage = when (chat.type) {
                MessageType.CHAT, MessageType.BOT_REQUEST -> {
                    user?.profileImageUrl
                }
                MessageType.BOT_RESPONSE -> {
                    chatBotImage
                }
            }
            ChatMessageResponseDTO.ChatInfoDto(
                userName = user?.nickname ?: "알 수 없음",
                userImage = profileImage,
                dateTime = chat.time,
                content = chat.message,
                isMy = user?.id == currentUser.id,
                type = chat.type
            )
        }

        val nextCursor = chatHistory.lastOrNull()?.id

        return ChatMessageResponseDTO.ListDto(dtoList, nextCursor)
    }




}