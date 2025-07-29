package busanVibe.busan.domain.festival.controller

import busanVibe.busan.domain.festival.dto.FestivalDetailsDTO
import busanVibe.busan.domain.festival.dto.FestivalListResponseDTO
import busanVibe.busan.domain.festival.enums.FestivalSortType
import busanVibe.busan.domain.festival.enums.FestivalStatus
import busanVibe.busan.domain.festival.service.FestivalQueryService
import busanVibe.busan.global.apiPayload.exception.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "축제 관련 API")
@RestController
@RequestMapping("/api/festivals")
class FestivalController(
    private val festivalQueryService: FestivalQueryService
) {

    @GetMapping
    @Operation(summary = "지역축제 목록 조회 API")
    fun festivalList(
        @RequestParam("sort", required = false) sort: FestivalSortType,
        @RequestParam("status", required = false)status: FestivalStatus
    ):ApiResponse<FestivalListResponseDTO.ListDto>?{

        val festivalList = festivalQueryService.getFestivalList(sort, status)
        return ApiResponse.onSuccess(festivalList);
    }

    @GetMapping("/{festivalId}")
    @Operation(summary = "지역축제 상세 조회")
    fun festivalDetails(
        @PathVariable("festivalId") festivalId: Long
    ):ApiResponse<FestivalDetailsDTO.DetailDto>{

        val festivalDetails = festivalQueryService.getFestivalDetails(festivalId)
        return ApiResponse.onSuccess(festivalDetails);
    }

}