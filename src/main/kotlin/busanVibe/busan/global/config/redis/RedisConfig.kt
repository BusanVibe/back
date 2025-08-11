package busanVibe.busan.global.config.redis

import busanVibe.busan.domain.chat.service.RedisSubscriber
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig(
    private val objectMapper: ObjectMapper
) {

    @Bean
    fun redisTemplate(connectionFactory: RedisConnectionFactory, genericJackson2JsonRedisSerializer: GenericJackson2JsonRedisSerializer): RedisTemplate<String, Any>{

        val redisTemplate = RedisTemplate<String, Any>()

        redisTemplate.connectionFactory = connectionFactory

        redisTemplate.keySerializer = StringRedisSerializer()
        redisTemplate.valueSerializer = genericJackson2JsonRedisSerializer
        redisTemplate.hashKeySerializer = StringRedisSerializer()
        redisTemplate.hashValueSerializer = genericJackson2JsonRedisSerializer

        return redisTemplate
    }

    @Bean
    fun redisMessageListenerContainer(
        connectionFactory: RedisConnectionFactory,
        listenerAdapter: MessageListenerAdapter
    ): RedisMessageListenerContainer {
        val container = RedisMessageListenerContainer()
        container.setConnectionFactory(connectionFactory)
        container.addMessageListener(listenerAdapter, ChannelTopic("chatroom"))
        return container
    }

    @Bean
    fun listenerAdapter(subscriber: RedisSubscriber, genericJackson2JsonRedisSerializer: GenericJackson2JsonRedisSerializer): MessageListenerAdapter {
        val adapter = MessageListenerAdapter(subscriber, "onMessage")
        adapter.setSerializer(genericJackson2JsonRedisSerializer)
        return adapter
    }

    @Bean
    fun topic(): ChannelTopic = ChannelTopic("chatroom")

    @Bean
    fun genericJackson2JsonRedisSerializer(): GenericJackson2JsonRedisSerializer {
        val mapper = objectMapper
            .registerModule(JavaTimeModule())
            .activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
            )
        return GenericJackson2JsonRedisSerializer(mapper)
    }

}