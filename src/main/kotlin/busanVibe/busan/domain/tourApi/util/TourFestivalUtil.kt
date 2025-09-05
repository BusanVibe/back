package busanVibe.busan.domain.tourApi.util

import busanVibe.busan.domain.tourApi.dto.FestivalApiResponse
import busanVibe.busan.domain.tourApi.dto.FestivalItem
import java.net.HttpURLConnection
import java.net.URL
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class TourFestivalUtil {

    @Value("\${tourAPI.key}")
    lateinit var tourApiKey: String

    fun getFestivals(pageSize: Int, pageNum: Int): List<FestivalItem> {

        val pageNum = pageNum
        val pageSize = pageSize

        val urlBuilder = StringBuilder("http://apis.data.go.kr/6260000/FestivalService/getFestivalKr")
        urlBuilder.append("?ServiceKey=")
        urlBuilder.append(tourApiKey)
        urlBuilder.append("&pageNo=")
        urlBuilder.append(pageNum)
        urlBuilder.append("&numOfRows=")
        urlBuilder.append(pageSize)
        urlBuilder.append("&resultType=json")


        println("url = ${urlBuilder.toString()}")

        val conn = URL(urlBuilder.toString()).openConnection() as HttpURLConnection
        conn.requestMethod = "GET"
        conn.setRequestProperty("Content-type", "application/json")

        val response = conn.inputStream.bufferedReader().use { it.readText() }
        conn.disconnect()

        val mapper = jacksonObjectMapper()
        val apiResponse: FestivalApiResponse = mapper.readValue(response)

        return apiResponse.getFestivalKr.item
    }


}