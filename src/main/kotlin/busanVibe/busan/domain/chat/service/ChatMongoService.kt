package busanVibe.busan.domain.chat.service

import busanVibe.busan.domain.chat.domain.ChatMessage
import busanVibe.busan.domain.chat.dto.websocket.ChatMessageReceiveDTO
import busanVibe.busan.domain.chat.dto.websocket.ChatMessageResponseDTO
import busanVibe.busan.domain.chat.dto.websocket.ChatMessageSendDTO
import busanVibe.busan.domain.chat.enums.MessageType
import busanVibe.busan.domain.chat.repository.ChatMongoRepository
import busanVibe.busan.domain.user.repository.UserRepository
import busanVibe.busan.domain.user.service.login.AuthService
import busanVibe.busan.global.apiPayload.code.ErrorReasonDTO
import busanVibe.busan.global.apiPayload.code.status.ErrorStatus
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
            time = chatMessage.time?: LocalDateTime.now(),
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

    fun getChatHistory(): ChatMessageResponseDTO.ListDto {
        val currentUser = AuthService().getCurrentUser()

        val chatHistory = chatMongoRepository.findAllByOrderByTime()

        val dtoList = chatHistory.mapNotNull { chat ->
            val user = chat.userId?.let { userRepository.findById(it).orElse(null) }

            if (user == null) return@mapNotNull null

            ChatMessageResponseDTO.ChatInfoDto(
                userName = user.nickname,
                userImage = user.profileImageUrl,
                content = chat.message,
                dateTime = chat.time,
                isMy = user.id == currentUser.id
            )
        }

        return ChatMessageResponseDTO.ListDto(dtoList)
    }




}