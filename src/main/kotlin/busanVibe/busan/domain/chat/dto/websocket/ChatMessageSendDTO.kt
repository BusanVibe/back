package busanVibe.busan.domain.chat.dto.websocket

import busanVibe.busan.domain.chat.enums.MessageType
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.time.LocalDateTime

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class ChatMessageSendDTO(
//    var userId: Long? = null,
    var type: MessageType? = null,     // 메시지 타입
    var message: String? = null,       // 메시지
    var time: LocalDateTime? = null,          // 전송 시간
){

}