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
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ChatMongoService(
    private val chatMongoRepository: ChatMongoRepository,
    private val redisPublisher: RedisPublisher,
    private val topic: ChannelTopic,
    private val userRepository: UserRepository,
    private val openAiService: OpenAiService
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
        // '/'로 시작하면 챗봇(BOT)
        val type: MessageType = if(message[0] == '/'){
            MessageType.BOT
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

        // 일반 채팅일 경우 채팅 기록 저장
        chatMongoRepository.save(chat)

        // 유저들에게 웹소켓으로 전달할 메시지의 DTO 생성
        val receiveDto = buildReceiveDto(type, currentUser, chat, now)

        // 일반 채팅일 경우에만 유저들에게 웹소켓 메시지 보냄
        if(type == MessageType.CHAT) {
            redisPublisher.publish(topic, receiveDto)
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
                imageUrl = null,
                message = message,
                time = timestamp,
                type = type,
                userId = -1
            )
        }
    }

    fun getChatHistory(cursorId: String?, pageSize: Int = 15): ChatMessageResponseDTO.ListDto {

        if(pageSize < 0){
            throw GeneralException(ErrorStatus.INVALID_PAGE_SIZE_MINUS)
        }else if (pageSize > 30) {
            throw GeneralException(ErrorStatus.INVALID_PAGE_SIZE_BIG)
        }

        // 현재 로그인한 유저 조회
        val currentUser = AuthService().getCurrentUser()

        // 조회 -> List<ChatMessage> 변수 선언 및 초기화
        val chatHistory: List<ChatMessage> = if (cursorId != null) { // 처음이면 cursorId 없이 조회
            chatMongoRepository.findByIdLessThanOrderByTimeDesc(cursorId, Pageable.ofSize(pageSize))
        } else { // 처음 아니면 cursorId로 조회
            chatMongoRepository.findAllByOrderByTimeDesc(Pageable.ofSize(pageSize))
        }

        // userId List 저장. 바로 뒤에 userId로 User 정보들을 먼저 찾고, 그 뒤에 DTO 변환
        val userIdList: List<Long> = chatHistory.mapNotNull { it -> it.userId }.toList()

        // <userId, User> -> Map<Long, User>로 저장
        val userMap: Map<Long, User> = userRepository.findUsersByIdIn(userIdList).associateBy { it.id as Long }

        // DTO 변환
        val dtoList = chatHistory.map { chat ->
            val user: User? = userMap[chat.userId ?: -1]
            ChatMessageResponseDTO.ChatInfoDto(
                userName = user?.nickname ?: "알 수 없음",
                userImage = user?.profileImageUrl,
                dateTime = chat.time,
                content = chat.message,
                isMy = user?.id == currentUser.id
            )
        }

        val nextCursor = chatHistory.lastOrNull()?.id

        return ChatMessageResponseDTO.ListDto(dtoList, nextCursor)
    }




}