package busanVibe.busan.domain.chat.controller

import busanVibe.busan.domain.chat.dto.websocket.ChatMessageResponseDTO
import busanVibe.busan.domain.chat.dto.websocket.ChatMessageSendDTO
import busanVibe.busan.domain.chat.service.ChatMongoService
import busanVibe.busan.global.apiPayload.exception.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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

    val log: Logger = LoggerFactory.getLogger(ChatRestController::class.java)

    @PostMapping("/send")
    @Operation(summary = "채팅 전송 API",
                description = """
                    채팅 전송 API 입니다. 해당 API 호출 시, 메시지가 전송되고, 채팅 웹소켓에 연결된 유저들에게 메시지가 전송됩니다.
                    메시지의 길이는 <b>최대 200글자</b>입니다.
                    type 유형 : ['CHAT', 'BOT']
                    - CHAT (구현O): 일반 채팅 
                    - BOT  (구현X): 챗봇 기능입니다. 본인에게만 웹소켓 메시지가 전송되고, 채팅방을 나갈 시 다시 볼 수 없습니다.
                """)
    fun sendMessage(@RequestBody chatMessage: ChatMessageSendDTO) {
        log.info("POST /chat/send - 메시지 수신: $chatMessage")
        chatMongoService.saveAndPublish(chatMessage)
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