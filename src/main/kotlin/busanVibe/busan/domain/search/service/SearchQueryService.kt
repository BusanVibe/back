package busanVibe.busan.domain.search.service

import busanVibe.busan.domain.common.dto.InfoType
import busanVibe.busan.domain.festival.repository.FestivalLikesRepository
import busanVibe.busan.domain.festival.repository.FestivalRepository
import busanVibe.busan.domain.place.enums.PlaceType
import busanVibe.busan.domain.place.repository.PlaceLikeRepository
import busanVibe.busan.domain.place.repository.PlaceRepository
import busanVibe.busan.domain.search.dto.SearchResultDTO
import busanVibe.busan.domain.search.enums.GeneralSortType
import busanVibe.busan.domain.search.util.SearchUtil
import busanVibe.busan.domain.user.service.login.AuthService
import busanVibe.busan.global.apiPayload.code.status.ErrorStatus
import busanVibe.busan.global.apiPayload.exception.handler.ExceptionHandler
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SearchQueryService(
    private val placeRepository: PlaceRepository,
    private val festivalRepository: FestivalRepository,
    private val placeLikeRepository: PlaceLikeRepository,
    private val festivalLikeRepository: FestivalLikesRepository,
    private val searchUtil: SearchUtil,
) {

    @Transactional(readOnly = true)
    fun getSearchResult(infoType: InfoType, sort: GeneralSortType, keyword: String): SearchResultDTO.ListDto {
        val currentUser = AuthService().getCurrentUser()

        // 축제 + 혼잡도 검색 예외 처리
        if(infoType == InfoType.FESTIVAL && sort == GeneralSortType.CONGESTION) {
            throw ExceptionHandler(ErrorStatus.SEARCH_INVALID_CONDITION)
        }

        // 명소 타입별 조회
        val places = when (infoType) {
            InfoType.ALL -> placeRepository.findAllWithLikesAndOpenTime()
//            InfoType.CAFE -> placeRepository.findAllWithLikesAndOpenTimeByType(PlaceType.CAFE)
            InfoType.RESTAURANT -> placeRepository.findAllWithLikesAndOpenTimeByType(PlaceType.RESTAURANT)
            InfoType.SIGHT -> placeRepository.findAllWithLikesAndOpenTimeByType(PlaceType.SIGHT)
            else -> emptyList()
        }

        // 축제 조회
        val festivals = when (infoType) {
            InfoType.ALL, InfoType.FESTIVAL -> festivalRepository.findAllWithFetch()
            else -> emptyList()
        }

        // 좋아요 정보 조회
        val placeLikeList = placeLikeRepository.findLikeByPlace(places)
        val festivalLikeList = festivalLikeRepository.findLikeByFestival(festivals)

        // 엔티티 List로 DTO List 생성
        val infoDtoList = searchUtil.listToSearchDTO(places, festivals, sort, currentUser, placeLikeList, festivalLikeList)

        // 키워드 필터링 후 List 생성
        val resultList: MutableList<SearchResultDTO.InfoDto> =
            if (keyword.isNotEmpty()) { // 키워드가 존재하면, 이름에 키워드가 존재하지 않는 항목들을 제거
                infoDtoList.toMutableList().apply { removeIf { !containKeyword(it, keyword) } }
            } else {
                infoDtoList.toMutableList()
            }

        // 반환
        return SearchResultDTO.ListDto(sort.name, resultList)
    }

    private fun containKeyword(infoDto: SearchResultDTO.InfoDto, keyword: String): Boolean {
        val name = infoDto.name.replace(" ", "").lowercase()
        val key = keyword.replace(" ", "").lowercase()
        return name.contains(key)
    }

}