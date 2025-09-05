package busanVibe.busan.domain.place.util

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate

// 이미지 정보가 "" 일 때는 null을 반환하는 메서드
fun String?.nullIfBlank(): String? = this?.takeIf { it.isNotBlank() }?.trim()

fun checkImageUrl(url: String?): Boolean {
    if (url.isNullOrBlank()) return false
    return try {
        val restTemplate = RestTemplate()
        val uri = java.net.URI.create(url)
        val response: ResponseEntity<ByteArray> = restTemplate.getForEntity(uri, ByteArray::class.java)

        val contentType = response.headers.contentType?.toString() ?: ""
        val isImage = contentType.startsWith("image/")
//        println("${url} - ${if (isImage) "O" else "X"} - $contentType")

        isImage && response.statusCode == HttpStatus.OK
    } catch (e: Exception) {
//        println("${url} - X - ${e.message}")
        false
    }
}



