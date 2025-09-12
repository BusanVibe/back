package busanVibe.busan.domain.home.controller

import busanVibe.busan.domain.home.dto.HomeResponseDTO
import busanVibe.busan.domain.home.enums.CurationType
import busanVibe.busan.domain.home.service.HomeQueryService
import busanVibe.busan.global.apiPayload.exception.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
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
    @Operation(summary = "큐레이션 조회 API",
            description =
                    """
                        명소 혹은 축제에 대한 큐레이션 정보를 반환합니다.
                        type이 필요합니다. - [ PLACE, FESTIVAL ]
                        ( 이미지 없는것은 베재, 장소는 관광지만 조회 )                    
                    """
    )
    fun getHomeCuration(@RequestParam("type", required = true) type: CurationType): ApiResponse<HomeResponseDTO.CurationList>{
        val curations = homeQueryService.getCurations(type)
        return ApiResponse.onSuccess(curations)
    }

}