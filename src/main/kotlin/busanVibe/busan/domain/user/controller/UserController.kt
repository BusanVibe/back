package busanVibe.busan.domain.user.controller

import busanVibe.busan.domain.common.dto.InfoType
import busanVibe.busan.domain.search.dto.SearchResultDTO
import busanVibe.busan.domain.search.enums.GeneralSortType
import busanVibe.busan.domain.user.data.dto.UserResponseDTO
import busanVibe.busan.domain.user.service.UserQueryService
import busanVibe.busan.global.apiPayload.exception.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "마이페이지 API")
@RestController
@RequestMapping("/api/users")
class UserController(
    private val userQueryService: UserQueryService
) {

    @GetMapping("/mypage")
    @Operation(summary = "마이페이지 조회 API", description = "it is what it is")
    fun myPage(): ApiResponse<UserResponseDTO.MyPageDto> {
        val myPageInfo = userQueryService.getMyPageInfo()
        return ApiResponse.onSuccess(myPageInfo)
    }

    @GetMapping("/likes")
    @Operation(summary = "좋아요 목록 조회 API",
        description = """
            내가 좋아요 누른 정보 조회 API
            CONGESTION(혼잡도) 정렬조건은 지원하지 않습니다.
        """)
    fun likes(@RequestParam("option", defaultValue = "ALL") infoType: InfoType,
              @RequestParam("sort", defaultValue = "DEFAULT") sort: GeneralSortType
    ): ApiResponse<SearchResultDTO.ListDto> {

        val likeInfo = userQueryService.getMyLikes(infoType, sort)
        return ApiResponse.onSuccess(likeInfo)

    }


}