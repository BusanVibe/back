package busanVibe.busan.domain.tourApi.util

import busanVibe.busan.domain.place.enums.PlaceType
import busanVibe.busan.domain.tourApi.dto.PlaceApiResponseWrapper
import busanVibe.busan.domain.tourApi.dto.PlaceCommonApiWrapper
import busanVibe.busan.domain.tourApi.dto.PlaceImageResponseWrapper
import busanVibe.busan.domain.tourApi.dto.PlaceIntroductionResponseWrapper
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.net.URI

@Component
class TourPlaceUtil(
    private val webClientBuilder: WebClient.Builder,
) {

    @Value("\${tourAPI.key}")
    lateinit var tourApiKey: String

    // SLF4J 로그 세팅
    private val log: Logger = LoggerFactory.getLogger(TourPlaceUtil::class.java)

    // TOUR API 요청 파라미터
    private val mobileOs:String = "AND"
    private val mobileApp: String = "busanvibe"

    // webclient 응답 버퍼 증가
    private val strategies = org.springframework.web.reactive.function.client.ExchangeStrategies.builder()
        .codecs { configurer ->
            configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024) // 16MB로 늘림
        }
        .build()

    // json 변환 위한 objectMapper
    val objectMapper = jacksonObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)


    fun getPlace(placeType: PlaceType, pageSize: Int, pageNum: Int): PlaceApiResponseWrapper {
        val placeTypeCode = placeType.tourApiTypeId

        val url = StringBuilder("https://apis.data.go.kr/B551011/KorService2/areaBasedList2")
            .append("?numOfRows=").append(pageSize)
            .append("&pageNo=").append(pageNum)
            .append("&MobileOS=").append(mobileOs)
            .append("&MobileApp=").append(mobileApp)
            .append("&contentTypeId=").append(placeTypeCode)
            .append("&areaCode=6") // 부산 지역
            .append("&_type=json")  // JSON 요청
            .append("&serviceKey=").append(tourApiKey)
            .toString()

        val json = makeJson(url) ?: throw RuntimeException("JSON response is null")
        log.info("[TourPlaceUtil] Tour API 지역기반 API 조회 결과 json = {}", json)
        return objectMapper.readValue(json, PlaceApiResponseWrapper::class.java)
    }

    fun getPlaceDetail(contentId: Long): PlaceCommonApiWrapper {
        val url = StringBuilder("https://apis.data.go.kr/B551011/KorService2/detailCommon2")
            .append("?MobileOS=").append(mobileOs)
            .append("&MobileApp=").append(mobileApp)
            .append("&contentId=").append(contentId)
            .append("&_type=json")
            .append("&serviceKey=").append(tourApiKey)
            .toString()

        val json = makeJson(url) ?: throw RuntimeException("JSON response is null")
        println("json = ${json}")
        return objectMapper.readValue(json, PlaceCommonApiWrapper::class.java)
    }

    fun getPlaceIntro(contentId: Long, contentTypeId: String): PlaceIntroductionResponseWrapper {
        val url = StringBuilder("https://apis.data.go.kr/B551011/KorService2/detailIntro2")
            .append("?MobileOS=").append(mobileOs)
            .append("&MobileApp=").append(mobileApp)
            .append("&contentId=").append(contentId)
            .append("&contentTypeId=").append(contentTypeId)
            .append("&_type=json")
            .append("&serviceKey=").append(tourApiKey)
            .toString()

        val json = makeJson(url) ?: throw RuntimeException("JSON response is null")
        return objectMapper.readValue(json, PlaceIntroductionResponseWrapper::class.java)
    }

    fun getImages(contentId: Long): PlaceImageResponseWrapper{
        val url = StringBuilder("https://apis.data.go.kr/B551011/KorService2/detailImage2")
            .append("?MobileOS=").append(mobileOs)
            .append("&MobileApp=").append(mobileApp)
            .append("&contentId=").append(contentId)
            .append("&_type=json")
            .append("&serviceKey=").append(tourApiKey)
            .toString()

        val json = makeJson(url) ?: throw RuntimeException("JSON response is null")
        return objectMapper.readValue(json, PlaceImageResponseWrapper::class.java)
    }

    private fun makeJson(url: String): String? {

        return webClientBuilder
            .exchangeStrategies(strategies)
            .build()
            .get()
            .uri(URI.create(url))
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(String::class.java)
            .block()
    }
}
