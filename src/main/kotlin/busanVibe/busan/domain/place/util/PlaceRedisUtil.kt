package busanVibe.busan.domain.place.util

import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDateTime

@Component
class PlaceRedisUtil(
    private val redisTemplate: StringRedisTemplate,
) {

    private val log = LoggerFactory.getLogger("busanVibe.busan.domain.place")

    // 임의로 혼잡도 생성하여 반환. 레디스 키 값으로 저장함.
//    fun getRedisCongestion(placeId: Long?): Int{
//
//        val key = "place:congestion:$placeId"
//        val randomCongestion = getRandomCongestion().toInt().toString()
//
//        redisTemplate.opsForValue()
//            .set(key, randomCongestion)
//
//        return Integer.parseInt(randomCongestion)
//    }

    // 지정 시간 혼잡도 조회
    // null이면 현재시간 기준
    fun getTimeCongestion(placeId: Long?, dateTime: LocalDateTime?): Float {

        val dateTime = dateTime ?: LocalDateTime.now()

        val roundedHour = (dateTime.hour / 3) * 3
        val key = "place:congestion:$placeId-${dateTime.year}-${dateTime.monthValue}-${dateTime.dayOfMonth}-$roundedHour"

        val value = redisTemplate.opsForValue().get(key)

        return (if (value != null) {
            value.toFloatOrNull() ?: 0
        } else {
            setPlaceTimeCongestion(placeId, dateTime.withHour(roundedHour))
            val newValue = redisTemplate.opsForValue().get(key)
            newValue?.toFloatOrNull() ?: 0
        }) as Float
    }

    fun getTimeCongestion(placeId: Long?):Float{
        return getTimeCongestion(placeId, LocalDateTime.now())
    }

    // 시간 혼잡도 설정
    private fun setPlaceTimeCongestion(placeId: Long?, dateTime: LocalDateTime) {
        val roundedHour = (dateTime.hour / 3) * 3
        val key = "place:congestion:$placeId-${dateTime.year}-${dateTime.monthValue}-${dateTime.dayOfMonth}-$roundedHour"
        val congestion = getRandomCongestion().toString()
        val success = redisTemplate.opsForValue().setIfAbsent(key, congestion, Duration.ofHours(24))

        if (success == true) {
            log.info("혼잡도 기록 저장 완료: $key, 저장된 혼잡도: $congestion")
        } else {
            val existing = redisTemplate.opsForValue().get(key)
            log.info("이미 존재하는 혼잡도 기록: $key, 기존 혼잡도: $existing")
        }
    }

    // 혼잡도 생성 (1.0 ~ 5.0 사이의 Float)
    private fun getRandomCongestion(): Float {
        return (Math.random() * 4 + 1).toFloat()
    }


}