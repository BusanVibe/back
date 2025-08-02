package busanVibe.busan.domain.common.dto

enum class InfoType(
    val kr: String,
    val en: String,
) {
    ALL("전체", "ALL"),
    // 명소
    SIGHT("명소", "SIGHT"),
    RESTAURANT("식당", "RESTAURANT"),
    CAFE("카페", "CAFE"),
    // 축제
    FESTIVAL("축제", "FESTIVAL"),
    ;

}