package busanVibe.busan.domain.place.controller

import busanVibe.busan.domain.place.dto.PlaceMapResponseDTO
import busanVibe.busan.domain.place.enums.PlaceType
import busanVibe.busan.domain.place.service.PlaceCongestionQueryService
import busanVibe.busan.global.apiPayload.exception.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/congestion")
class PlaceCongestionController (
    private val placeCongestionQueryService: PlaceCongestionQueryService,
){

    @GetMapping
    @Operation(summary = "지도 조회")
    fun map(
        @RequestParam("type", required = false, defaultValue = "ALL") type: PlaceType,
        @RequestParam("latitude")latitude: Double,
        @RequestParam("longitude")longtitude: Double): ApiResponse<PlaceMapResponseDTO.MapListDto>?{

        val places = placeCongestionQueryService.getMap(type, latitude, longtitude)
        return ApiResponse.onSuccess(places);
    }

    @GetMapping("/place/{placeId}")
    @Operation(summary = "명소 기본 정보 조회")
    fun placeDefaultInfo(@PathVariable("placeId") placeId: Long): ApiResponse<PlaceMapResponseDTO.PlaceDefaultInfoDto>?{
        return null;
    }

    @GetMapping("/place/{placeId}/read-time")
    @Operation(summary = "명소 실시간 혼잡도 조회")
    fun placeRealTimeCongestion(
        @PathVariable("placeId") placeId: Long,
        @RequestParam("standard-time") standardTime: Integer): ApiResponse<PlaceMapResponseDTO.PlaceCongestionDto>?{
        return null;
    }

    @GetMapping("/place/{placeId}/distribution")
    @Operation(summary = "명소 이용객 분포 조회")
    fun placeUsesDistribution(@PathVariable("placeId") placeId: Long): ApiResponse<PlaceMapResponseDTO.PlaceUserDistributionDto>?{
        return null;
    }

}