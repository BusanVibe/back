package busanVibe.busan.domain.tourApi.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class PlaceIntroductionResponseWrapper(
    val response: PlaceIntroductionResponse
)

data class PlaceIntroductionResponse(
    val header: PlaceIntroductionHeader?,
    val body: PlaceIntroductionBody?
)

data class PlaceIntroductionHeader(
    @JsonProperty("resultCode")
    val resultCode: String,

    @JsonProperty("resultMsg")
    val resultMsg: String
)

data class PlaceIntroductionBody(
    val items: PlaceIntroductionItems,
    val numOfRows: Int,
    val pageNo: Int,
    val totalCount: Int
)

data class PlaceIntroductionItems(
    val item: List<PlaceIntroductionItem>
)

data class PlaceIntroductionItem(
    // 공통
    @JsonProperty("contentid")
    val contentId: String?,
    @JsonProperty("contenttypeid")
    val contentTypeId: String?,

    // 음식점
    @JsonProperty("firstmenu")
    val firstMenu: String?,
    @JsonProperty("treatmenu")
    val treatMenu: String?,
    @JsonProperty("opentimefood")
    val openTimeFood: String?,
    @JsonProperty("restdatefood")
    val restDateFood: String?,
    @JsonProperty("parkingfood")
    val parkingFood: String?,
    @JsonProperty("chkcreditcardfood")
    val chkCreditCardFood: String?,
    @JsonProperty("reservationfood")
    val reservationFood: String?,
    @JsonProperty("infocenterfood")
    val infoCenterFood: String?,
    @JsonProperty("scalefood")
    val scaleFood: String?,
    val seat: String?,
    val smoking: String?,
    @JsonProperty("kidsfacility")
    val kidsFacility: String?,

    // 숙박
    @JsonProperty("roomcount")
    val roomCount: String?,
    @JsonProperty("roomtype")
    val roomType: String?,
    @JsonProperty("checkintime")
    val checkInTime: String?,
    @JsonProperty("checkouttime")
    val checkOutTime: String?,
    @JsonProperty("parkinglodging")
    val parkingLodging: String?,
    @JsonProperty("infocenterlodging")
    val infoCenterLodging: String?,
    @JsonProperty("reservationlodging")
    val reservationLodging: String?,

    // 축제/행사
    @JsonProperty("eventstartdate")
    val eventStartDate: String?,
    @JsonProperty("eventenddate")
    val eventEndDate: String?,
    @JsonProperty("eventplace")
    val eventPlace: String?,
    @JsonProperty("eventhomepage")
    val eventHomepage: String?,
    val sponsor1: String?,
    @JsonProperty("sponsor1tel")
    val sponsor1Tel: String?,

    // 레포츠
    @JsonProperty("usetimeleports")
    val useTimeLeports: String?,
    @JsonProperty("parkingleports")
    val parkingLeports: String?,

    // 쇼핑
    @JsonProperty("opendateshopping")
    val openDateShopping: String?,
    @JsonProperty("restdateshopping")
    val restDateShopping: String?,
    @JsonProperty("opentime")
    val openTime: String?,
    @JsonProperty("parkingshopping")
    val parkingShopping: String?,
    @JsonProperty("saleitem")
    val saleItem: String?,

    // 문화시설
    @JsonProperty("usetimeculture")
    val useTimeCulture: String?,
    @JsonProperty("restdateculture")
    val restDateCulture: String?,
    @JsonProperty("infocenterculture")
    val infoCenterCulture: String?,
    @JsonProperty("parkingculture")
    val parkingCulture: String?,

    // 공통
    @JsonProperty("accomcount")
    val accomCount: String?,
    @JsonProperty("expagerange")
    val expAgeRange: String?,
    @JsonProperty("infocenter")
    val infoCenter: String?,
    @JsonProperty("usetime")
    val useTime: String?,
    @JsonProperty("restdate")
    val restDate: String?,
    val parking: String?
)
