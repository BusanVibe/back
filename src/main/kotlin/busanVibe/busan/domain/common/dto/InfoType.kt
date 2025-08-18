package busanVibe.busan.domain.common.dto

import busanVibe.busan.domain.place.enums.PlaceType

enum class InfoType(
    val kr: String,
    val en: String,
    val placeType: PlaceType? = null
) {
    ALL("전체", "ALL", PlaceType.ALL),
    // 명소
    SIGHT("명소", "SIGHT", PlaceType.SIGHT),
    RESTAURANT("식당", "RESTAURANT", PlaceType.RESTAURANT),
    CAFE("카페", "CAFE", PlaceType.CAFE),
    CULTURE("문화시설", "CULTURE", PlaceType.CULTURE),
    // 축제
    FESTIVAL("축제", "FESTIVAL"),
    ;

}