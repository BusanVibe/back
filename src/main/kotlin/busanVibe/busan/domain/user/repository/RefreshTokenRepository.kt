package busanVibe.busan.domain.user.repository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit

@Repository
class RefreshTokenRepository{

    private val redisTemplate: StringRedisTemplate

    @Autowired
    constructor(redisTemplate: StringRedisTemplate) {
        this.redisTemplate = redisTemplate
    }

    fun saveToken(userId: Long, refreshToken: String, expiration: Long) {
        val key:String = "refreshToken:$refreshToken"

        redisTemplate
            .opsForValue()
            .set(key,userId.toString(), expiration/1000, TimeUnit.SECONDS)
    }

    fun getUserIdByToken(refreshToken: String): Long? {
        val key:String = "refreshToken:$refreshToken"
        val userIdStr:String? = redisTemplate.opsForValue().get(key)
        return userIdStr?.toLongOrNull()
    }

}