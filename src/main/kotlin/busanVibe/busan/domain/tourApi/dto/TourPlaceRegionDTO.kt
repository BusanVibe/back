package busanVibe.busan.domain.tourApi.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class PlaceApiResponseWrapper(
    val response: PlaceApiResponse
)

data class PlaceApiResponse(
    val header: PlaceApiHeader?,
    val body: PlaceApiBody?
)

data class PlaceApiHeader(
    val resultCode: String,
    val resultMsg: String
)

data class PlaceApiBody(
    @JsonProperty("items")
    val items: PlaceApiItems?,
    @JsonProperty("numOfRows")
    val numOfRows: Int?,
    @JsonProperty("pageNo")
    val pageNo: Int?,
    @JsonProperty("totalCount")
    val totalCount: Int?
)

data class PlaceApiItems(
    @JsonProperty("item")
    val item: List<PlaceApiItem>
)

data class PlaceApiItem(
    @JsonProperty("lclsSystm3") val lclsSystm3: String?,
    @JsonProperty("firstimage") val firstImage: String?,
    @JsonProperty("firstimage2") val firstImage2: String?,
    @JsonProperty("mapx") val mapX: String?,
    @JsonProperty("mapy") val mapY: String?,
    @JsonProperty("mlevel") val mLevel: String?,
    @JsonProperty("addr2") val addr2: String?,
    @JsonProperty("areacode") val areaCode: String?,
    @JsonProperty("modifiedtime") val modifiedTime: String?,
    @JsonProperty("cpyrhtDivCd") val cpyrhtDivCd: String?,
    @JsonProperty("cat1") val cat1: String?,
    @JsonProperty("sigungucode") val sigunguCode: String?,
    @JsonProperty("tel") val tel: String?,
    @JsonProperty("title") val title: String,
    @JsonProperty("addr1") val addr1: String?,
    @JsonProperty("cat2") val cat2: String?,
    @JsonProperty("cat3") val cat3: String?,
    @JsonProperty("contentid") val contentId: Long,
    @JsonProperty("contenttypeid") val contentTypeId: String?,
    @JsonProperty("createdtime") val createdTime: String?,
    @JsonProperty("zipcode") val zipCode: String?,
    @JsonProperty("lDongRegnCd") val lDongRegnCd: String?,
    @JsonProperty("lDongSignguCd") val lDongSignguCd: String?,
    @JsonProperty("lclsSystm1") val lclsSystm1: String?,
    @JsonProperty("lclsSystm2") val lclsSystm2: String?
)
