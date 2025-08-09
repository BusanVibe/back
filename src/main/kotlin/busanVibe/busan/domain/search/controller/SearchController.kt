package busanVibe.busan.domain.search.controller

import busanVibe.busan.domain.common.dto.InfoType
import busanVibe.busan.domain.search.dto.SearchResultDTO
import busanVibe.busan.domain.search.enums.GeneralSortType
import busanVibe.busan.domain.search.service.SearchQueryService
import busanVibe.busan.global.apiPayload.exception.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "검색 관련 API")
@RestController
@RequestMapping("/api/search")
class SearchController(
    private val searchQueryService: SearchQueryService
) {

    @GetMapping("/search")
    fun searchResult(@RequestParam("option", defaultValue = "ALL") infoType: InfoType,
                           @RequestParam("sort", defaultValue = "DEFAULT") sort: GeneralSortType): ApiResponse<SearchResultDTO.ListDto>{

        val searchResult = searchQueryService.getSearchResult(infoType, sort)
        return ApiResponse.onSuccess(searchResult)

    }

}