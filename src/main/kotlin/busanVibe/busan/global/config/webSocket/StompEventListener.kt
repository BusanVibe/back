package busanVibe.busan.global.config.webSocket

import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component
import org.springframework.web.socket.messaging.SessionConnectedEvent

@Component
class StompEventListener(
    private val simpleMessageTemplate: SimpMessagingTemplate
) {

    private val log = LoggerFactory.getLogger(StompEventListener::class.java)

    @EventListener
    fun handleConnectEvent(event: SessionConnectedEvent){
        log.info("ebSocket connected: {}", event.message.headers)
    }

    @EventListener
    fun handleDisconnectEvent(event: SessionConnectedEvent){
        log.info("ebSocket disconnected: {}", event.message.headers.id)
    }


}