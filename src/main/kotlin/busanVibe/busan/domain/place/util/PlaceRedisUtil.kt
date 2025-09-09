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

    // 현재 시간 기준 혼잡도 구하는 메서드
    // 현재에 해당하는 요일과 시간의 혼잡도 반환
    fun getTimeCongestion(placeId: Long?):Float{
        return getTimeCongestion(placeId, LocalDateTime.now())
    }

    /**
    * [요일(1=Mon..7=Sun), 시간]으로 혼잡도 구하는 메서드
    * @param placeId 조회 대상 장소 ID
    * @param dayOfWeek 1..7 (1=월요일, 7=일요일)
    * @param hour 0..23
    */
    fun getTimeCongestion(placeId: Long?, day: Int, hour: Int): Float{
        val now = LocalDateTime.now()
        return getTimeCongestion(
            placeId,
            LocalDateTime.of(
                now.year,
                now.month,
                day,
                hour,
                now.minute
            )
        )
    }

    // 지정 시간 혼잡도 조회
    fun getTimeCongestion(placeId: Long?, dateTime: LocalDateTime?): Float {

        // DateTime
        val dateTime = dateTime ?: LocalDateTime.now()

        val key = getCongestionRedisKey(placeId, dateTime)
        val value = redisTemplate.opsForValue().get(key)

        val parsed = value?.toFloatOrNull()

        // 값이 존재하교 유효하다면
        if (parsed != null)return parsed // 그대로 반환

        // 값이 없다면
        setPlaceTimeCongestion(placeId, dateTime) // 새로 만들어서 할당
        val newValue = redisTemplate.opsForValue().get(key)?.toFloatOrNull() // 새로 설정한 값 조회
        return newValue ?: 0f // 반환, 값이 이상하다면 0 반환
    }

    // 시간 혼잡도 설정
    private fun setPlaceTimeCongestion(placeId: Long?, dateTime: LocalDateTime) {
        val key = getCongestionRedisKey(placeId, dateTime)
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
    private fun getRandomCongestion(): Float = (Math.random() * 4 + 1).toFloat()


    // redis에 저장할 key 생성
    private fun getCongestionRedisKey(placeId: Long?, dateTime: LocalDateTime): String
        = "place:congestion:${placeId}-${dateTime.dayOfWeek}-${dateTime.hour}"

}