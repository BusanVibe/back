package busanVibe.busan.domain.chat.dto.websocket

import busanVibe.busan.domain.chat.enums.MessageType
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class ChatMessageSendDTO(
//    var userId: Long? = null,
    var type: MessageType? = null,     // 메시지 타입
    @field:NotBlank(message = "빈 문자열을 전송할 수 없습니다.")
    @field:Size(min = 1, max = 200, message = "메세지의 길이는 1이상 200이하입니다.")
    var message: String? = null,       // 메시지
//    var time: LocalDateTime? = null,          // 전송 시간
){

}