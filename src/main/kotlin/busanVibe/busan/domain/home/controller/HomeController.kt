package busanVibe.busan.domain.home.controller

import busanVibe.busan.domain.home.dto.HomeResponseDTO
import busanVibe.busan.domain.home.service.HomeQueryService
import busanVibe.busan.global.apiPayload.exception.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "홈화면 API")
@RestController
@RequestMapping("/api/home")
class HomeController(
    private val homeQueryService: HomeQueryService
) {

    @GetMapping
    @Operation(summary = "홈화면 정보 조회 API", description = "지금 붐비는 곳과 추천 명소를 각각 5개씩 반환합니다.")
    fun getHomeInfo(): ApiResponse<HomeResponseDTO.HomeResultDto> {

        val homeInfo = homeQueryService.getHomeInfo()
        return ApiResponse.onSuccess(homeInfo)
    }

    @GetMapping("/curation")
    @Operation(summary = "홈화면 큐레이션 조회 API",
            description =
                    """
                        임의의 명소 1개, 축제 2개 반환
                        ( 이미지 없는것 베재, 장소는 관광지만 조회 )                    
                    """
    )
    fun getHomeCuration(): ApiResponse<HomeResponseDTO.CurationList>{
        val curations = homeQueryService.getCurations()
        return ApiResponse.onSuccess(curations)
    }

}