package busanVibe.busan.domain.place.controller

import busanVibe.busan.domain.place.dto.PlaceResponseDTO
import busanVibe.busan.domain.place.enums.PlaceType
import busanVibe.busan.domain.place.enums.PlaceSortType
import busanVibe.busan.domain.place.service.PlaceCommandService
import busanVibe.busan.domain.place.service.PlaceQueryService
import busanVibe.busan.global.apiPayload.exception.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "명소 관련 API")
@RestController
@RequestMapping("/api/places")
class PlaceController(
    val placeQueryService: PlaceQueryService,
    val placeCommandService: PlaceCommandService
) {

    @GetMapping
    @Operation(summary = "명소 목록 조회 API")
    fun getPlaceList(
        @RequestParam("category", required = false) type: PlaceType?,
        @RequestParam("sort", required = false) sort: PlaceSortType?
    ): ApiResponse<PlaceResponseDTO.PlaceListDto>?{


        val placeList = placeQueryService.getPlaceList(type, sort)
        return ApiResponse.onSuccess(placeList)

    }

    @GetMapping("/{placeId}")
    @Operation(summary = "명소 상세 조회 API",
        description =
        """
        명소 상세 조회 API 입니다. 타입 구분 없이 API 응답 형태가 동일합니다.
        응답 값 중 type 응답 종류 
        - 'SIGHT'
        - 'RESTAURANT'
        - 'CULTURE'
        """
    )
    fun getPlaceDetails(@PathVariable("placeId") placeId: Long) : ApiResponse<PlaceResponseDTO.PlaceDetailsDto>?
    {
        val placeDetail: PlaceResponseDTO.PlaceDetailsDto = placeQueryService.getPlaceDetails(placeId)
        return ApiResponse.onSuccess(placeDetail)
    }

    @PatchMapping("/like/{placeId}")
    @Operation(summary = "명소 좋아요 API",
                description = "명소 좋아요 API 입니다. 만약 이미 좋아요를 누른 상태라면 좋아요가 취소됩니다.")
    fun like(@PathVariable("placeId") placeId: Long?): ApiResponse<PlaceResponseDTO.LikeDto>{
        val likeDTO = placeCommandService.like(placeId)
        return ApiResponse.onSuccess(likeDTO)
    }



}