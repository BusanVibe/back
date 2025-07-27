package busanVibe.busan.domain.place.enums

import lombok.AllArgsConstructor

@AllArgsConstructor
enum class PlaceType(
    val korean: String
) {

    ALL("전체"),
    SIGHT("관광지"),
    RESTAURANT("식당"),
    CAFE("카페")
    ;




}