package busanVibe.busan.domain.place.controller

import busanVibe.busan.domain.place.dto.PlaceMapResponseDTO
import busanVibe.busan.domain.place.enums.PlaceType
import busanVibe.busan.domain.place.service.PlaceCongestionQueryService
import busanVibe.busan.global.apiPayload.exception.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "혼잡도+지도 관련 API")
@RestController
@RequestMapping("/api/congestion")
class PlaceCongestionController (
    private val placeCongestionQueryService: PlaceCongestionQueryService,
){

    @GetMapping
    @Operation(summary = "지도 조회",
        description =
        """
            지도에 띄울 장소를 조회합니다.
            좌측 상단의 좌표와 우측 하단의 위경도를 요청 파라미터로 포함하세요.
            좌측상단 ( lat1, lng1 ), 우측하단 ( lat2, lng2 )
            lat1 > lat2
            lng1 < lng2
        """
    )
    fun map(
        @RequestParam("type", required = false, defaultValue = "ALL") type: PlaceType,
        @RequestParam("lat1")lat1: Double,
        @RequestParam("lng1")lng1: Double,
        @RequestParam("lat2")lat2: Double,
        @RequestParam("lng2")lng2: Double
    ): ApiResponse<PlaceMapResponseDTO.MapListDto>{

        val places = placeCongestionQueryService.getMap(type, lat1, lng1, lat2, lng2)
        return ApiResponse.onSuccess(places);
    }

    @GetMapping("/place/{placeId}")
    @Operation(summary = "명소 기본 정보 조회")
    fun placeDefaultInfo(@PathVariable("placeId") placeId: Long): ApiResponse<PlaceMapResponseDTO.PlaceDefaultInfoDto>{

        val place = placeCongestionQueryService.getPlaceDefault(placeId)
        return ApiResponse.onSuccess(place)
    }

    @GetMapping("/place/{placeId}/congestions")
    @Operation(summary = "명소 혼잡도 조회",
        description =
            """
                * congestions_by_day : 각 요일별 현재시간 기준 혼잡도
                  - index [ 0:월, 1:화 ... 5:토, 6:일 ]
                  
                * congestions_by_time : 각 요일별 시간별 혼잡도 ( 0~23시 제공 )
                  - 명소의 모든 요일의 혼잡도 제공
                  - 총 7개의 배열, 각 배열에는 24개의 혼잡도 정보 담김
                  
                * standard_day와 standard_time 활용해서 현재시간 판단 가능합니다.
                  이거도 마찬가지로 0:월 ~ 6:일
                  
                * 현재(실시간) 혼잡도는 real_time_congestion_level 이용하면 됩니다.
            """
    )
    fun placeRealTimeCongestion(
        @PathVariable("placeId") placeId: Long): ApiResponse<PlaceMapResponseDTO.PlaceCongestionDto>{
        val congestion = placeCongestionQueryService.getCongestion(placeId)
        return ApiResponse.onSuccess(congestion)
    }

    @GetMapping("/place/{placeId}/distribution")
    @Operation(summary = "명소 이용객 분포 조회", description = "각 분포 항목의 백분율 정보를 반환합니다.")
    fun placeUsesDistribution(@PathVariable("placeId") placeId: Long): ApiResponse<PlaceMapResponseDTO.PlaceUserDistributionDto>?{
        val distribution = placeCongestionQueryService.getDistribution(placeId)
        return ApiResponse.onSuccess(distribution)
    }

}