package busanVibe.busan.domain.place.enums

import lombok.AllArgsConstructor

@AllArgsConstructor
enum class PlaceType(
    val korean: String,
    val capitalEnglish: String
) {

    ALL("전체", "ALL"),
    SIGHT("관광지", "SIGHT"),
    RESTAURANT("식당", "RESTAURANT"),
    CAFE("카페", "CAFE")
    ;




}