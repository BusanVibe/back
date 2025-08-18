package busanVibe.busan.domain.tourApi.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

data class FestivalApiResponse(
    val getFestivalKr: FestivalData
)

data class FestivalData(
    val header: FestivalHeader,
    val item: List<FestivalItem>,
    val numOfRows: Int,
    val pageNo: Int,
    val totalCount: Int
)

data class FestivalHeader(
    val code: String,
    val message: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class FestivalItem(
    val UC_SEQ: Long,
    val MAIN_TITLE: String?,
    val GUGUN_NM: String?,
    val LAT: Double?,
    val LNG: Double?,
    val PLACE: String?,
    val TITLE: String?,
    val SUBTITLE: String?,
    val MAIN_PLACE: String?,
    val ADDR1: String?,
    val ADDR2: String?,
    val CNTCT_TEL: String?,
    val HOMEPAGE_URL: String?,
    val TRFC_INFO: String?,
    val USAGE_DAY: String?,                       // 운영기간
    val USAGE_DAY_WEEK_AND_TIME: String?,         // 이용요일 및 시간
    val USAGE_AMOUNT: String?,                    // 이용요금
    val MAIN_IMG_NORMAL: String?,
    val MAIN_IMG_THUMB: String?,
    val ITEMCNTNTS: String?,                      // 상세내용
    val MIDDLE_SIZE_RM1: String?                  // 편의시설
)

