package busanVibe.busan.domain.place.service

import org.springframework.data.redis.core.StringRedisTemplate

class PlaceRedisUtil(
    private val redisTemplate: StringRedisTemplate
) {

    // 임의로 혼잡도 생성하여 반환. 레디스 키 값으로 저장함.
    fun getRedisCongestion(placeId: Long?): Int{

        val key = "place:congestion:$placeId"
        val randomCongestion: Int = (Math.random() * 5 + 1).toInt()

        redisTemplate.opsForValue()
            .set(key, randomCongestion.toString())

        return randomCongestion
    }

}