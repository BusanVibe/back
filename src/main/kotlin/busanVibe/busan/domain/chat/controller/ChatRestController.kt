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
import org.springframework.web.bind.annotation.RestController

@Tag(name = "채팅 관련 API")
@RestController
@RequestMapping("/api/chat")
class ChatRestController(
    private val chatMongoService: ChatMongoService,
) {

    val log: Logger = LoggerFactory.getLogger(ChatRestController::class.java)

    @PostMapping("/send")
    fun sendMessage(@RequestBody chatMessage: ChatMessageSendDTO) {
        log.info("POST /chat/send - 메시지 수신: $chatMessage")
        chatMongoService.saveAndPublish(chatMessage)
    }

    @GetMapping("/history")
    @Operation(summary = "채팅 조회 API", description = "채팅 목록을 조회합니다.")
    fun getHistory(): ApiResponse<ChatMessageResponseDTO.ListDto> {
        val chatList = chatMongoService.getChatHistory()
        return ApiResponse.onSuccess(chatList)
    }

}