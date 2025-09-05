package busanVibe.busan.domain.place.util

// 이미지 정보가 "" 일 때는 null을 반환하는 메서드
fun String?.nullIfBlank(): String? = this?.takeIf { it.isNotBlank() }?.trim()

