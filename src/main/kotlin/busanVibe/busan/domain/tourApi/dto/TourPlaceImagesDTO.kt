package busanVibe.busan.domain.tourApi.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class PlaceImageResponseWrapper(
    val response: PlaceImageResponse
)

data class PlaceImageResponse(
    val header: PlaceImageHeader?,
    val body: PlaceImageBody?
)

data class PlaceImageHeader(
    @JsonProperty("resultCode")
    val resultCode: String,
    @JsonProperty("resultMsg")
    val resultMsg: String
)

data class PlaceImageBody(
    val items: PlaceImageItems?,
    val numOfRows: Int,
    val pageNo: Int,
    val totalCount: Int
)

data class PlaceImageItems(
    val item: List<PlaceImageItem>
)

data class PlaceImageItem(
    @JsonProperty("contentid")
    val contentId: String?,
    @JsonProperty("originimgurl")
    val originImgUrl: String?,
    @JsonProperty("imgname")
    val imgName: String?,
    @JsonProperty("smallimageurl")
    val smallImageUrl: String?,
    @JsonProperty("cpyrhtDivCd")
    val cpyrhtDivCd: String?,
    val serialnum: String?
)
