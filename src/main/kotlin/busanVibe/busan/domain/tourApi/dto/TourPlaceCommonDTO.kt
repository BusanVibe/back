package busanVibe.busan.domain.tourApi.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class PlaceCommonApiWrapper(
    val response: PlaceCommonApiResponse
)

data class PlaceCommonApiResponse(
    val header: PlaceCommonApiHeader?,
    val body: PlaceCommonBody?
)

data class PlaceCommonApiHeader(
    @JsonProperty("resultCode")
    val resultCode: String,

    @JsonProperty("resultMsg")
    val resultMsg: String
)

data class PlaceCommonBody(
    val items: PlaceCommonApiItems,
    val numOfRows: Int,
    val pageNo: Int,
    val totalCount: Int
)

data class PlaceCommonApiItems(
    val item: List<PlaceCommonApiItem>
)

data class PlaceCommonApiItem(
    @JsonProperty("contentid")
    val contentId: Long,

    @JsonProperty("contenttypeid")
    val contentTypeId: Int? = null,

    val title: String,

    val createdtime: String? = null,
    val modifiedtime: String? = null,

    val tel: String? = null,
    val telname: String? = null,
    val homepage: String? = null,

    @JsonProperty("firstimage")
    val firstImage: String? = null,

    @JsonProperty("firstimage2")
    val firstImage2: String? = null,

    val cpyrhtDivCd: String? = null,
    val areacode: Int? = null,
    val sigungucode: Int? = null,
    val lDongRegnCd: Int? = null,
    val lDongSignguCd: Int? = null,
    val lclsSystm1: String? = null,
    val lclsSystm2: String? = null,
    val lclsSystm3: String? = null,
    val cat1: String? = null,
    val cat2: String? = null,
    val cat3: String? = null,
    val addr1: String? = null,
    val addr2: String? = null,
    val zipcode: String? = null,

    @JsonProperty("mapx")
    val mapX: BigDecimal? = null,

    @JsonProperty("mapy")
    val mapY: BigDecimal? = null,

    val mlevel: Int? = null,
    val overview: String? = null
)