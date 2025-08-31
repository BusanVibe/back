package busanVibe.busan.domain.chat.controller

import busanVibe.busan.domain.chat.dto.websocket.ChatMessageReceiveDTO
import busanVibe.busan.domain.chat.dto.websocket.ChatMessageResponseDTO
import busanVibe.busan.domain.chat.dto.websocket.ChatMessageSendDTO
import busanVibe.busan.domain.chat.service.ChatMongoService
import busanVibe.busan.global.apiPayload.exception.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "채팅 관련 API")
@RestController
@RequestMapping("/api/chat")
class   ChatRestController(
    private val chatMongoService: ChatMongoService,
) {

    @PostMapping("/send")
    @Operation(summary = "채팅 전송 API",
                description = """
                    채팅 전송 API 입니다. 해당 API 호출 시 메시지가 전송되고, 채팅 웹소켓에 연결된 유저들에게 메시지가 전송됩니다.
                    메시지의 길이는 최대 200글자입니다.
                    
                    type : ['CHAT', 'BOT_RESPONSE', 'BOT_REQUEST']
                    
                    메시지가 '/' 로 시작하면 챗봇 답변을 응답합니다.
                    일반 채팅은 웹소켓으로 메시지를 전송하고, 챗봇은 웹소켓 메시지를 전송하지 않습니다.
                    따라서 일반 채팅은 웹소켓으로 받은 메시지로 활용하고, 챗봇에게 받은 답변은 해당 API의 응답 결과를 활용해주세요.
                    
                """)
    fun sendMessage(@Valid @RequestBody chatMessage: ChatMessageSendDTO): ApiResponse<ChatMessageReceiveDTO> {
        val receiveDTO = chatMongoService.saveAndPublish(chatMessage)
        return ApiResponse.onSuccess(receiveDTO)
    }

    @GetMapping("/history")
    @Operation(summary = "채팅 조회 API",
        description = """
            채팅 목록을 조회합니다.
            - cursor 페이지네이션 처리를 합니다.
              처음 조회할 때는 cursor-id에 null이 들어가고, 이후에는 cursor-id에 응답받은 cursor_id를 넣어 요청하세요.
            - page-size의 기본값은 30이며 최대값은 30입니다. 
        """)
    fun getHistory(@RequestParam("cursor-id", required = false) cursorId: String?, @RequestParam("page-size", required = false, defaultValue = "30") size: Int): ApiResponse<ChatMessageResponseDTO.ListDto> {
        val chatList = chatMongoService.getChatHistory(cursorId, size)
        return ApiResponse.onSuccess(chatList)
    }

}