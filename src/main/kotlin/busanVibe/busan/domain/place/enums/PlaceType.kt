package busanVibe.busan.domain.place.enums

import lombok.AllArgsConstructor

@AllArgsConstructor
enum class PlaceType(
    val korean: String,
    val capitalEnglish: String,
    val tourApiTypeId: String,
    val placeUseTimeColumn: String,
    val restDateColumn: String,
) {

    ALL("전체", "ALL", "", "", ""),
    SIGHT("관광지", "SIGHT", "12", "useTime", "restDate"),
    RESTAURANT("식당", "RESTAURANT", "39", "openTimeFood", "restDateFood"),
//    CAFE("카페", "CAFE", "00", "openTimeFood", "restDateFood"),
    CULTURE("문화시설", "CULTURE", "14", "useTimeCulture", "restDateCulture")
    ;

    companion object{
        fun fromTourApiTypeId(code: String): PlaceType? {
            return values().find { it.tourApiTypeId == code }
        }
    }


}