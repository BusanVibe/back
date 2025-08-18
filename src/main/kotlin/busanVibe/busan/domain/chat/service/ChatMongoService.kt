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
import busanVibe.busan.global.apiPayload.code.ErrorReasonDTO
import busanVibe.busan.global.apiPayload.code.status.ErrorStatus
import busanVibe.busan.global.apiPayload.exception.GeneralException
import org.springframework.data.domain.Pageable
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.http.HttpStatus
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ChatMongoService(
    private val chatMongoRepository: ChatMongoRepository,
    private val redisPublisher: RedisPublisher,
    private val topic: ChannelTopic,
    private val userRepository: UserRepository,
    private val messagingTemplate: SimpMessagingTemplate
) {

    fun saveAndPublish(chatMessage: ChatMessageSendDTO) {

        // 현재 유저 조회
        val currentUser = AuthService().getCurrentUser()

        // 값 없으면 "" 저장
        val message = chatMessage.message ?: ""

        // 200자 넘어갈 시 예외처리
        if (message.length > 200) {
            val errorDTO = ErrorReasonDTO(
                httpStatus = HttpStatus.BAD_REQUEST,
                code = ErrorStatus.CHAT_INVALID_LENGTH.code,
                message = ErrorStatus.CHAT_INVALID_LENGTH.message,
                isSuccess = false
            )
            messagingTemplate.convertAndSend("/sub/chat/error", errorDTO)
            return  // 또는 throw 후 처리
        }

        // 채팅 객체 생성
        val document = ChatMessage(
            type = chatMessage.type?: MessageType.CHAT,
            userId = currentUser.id,
            message = chatMessage.message?:"",
            time = LocalDateTime.now(),
        )

        // 채팅 저장
        chatMongoRepository.save(document)

        val receiveDto = ChatMessageReceiveDTO(
            name = currentUser.nickname,
            imageUrl = currentUser.profileImageUrl,
            message = document.message,
            time = document.time,
            type = document.type,
            userId = currentUser.id
        )

        redisPublisher.publish(topic, receiveDto)
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