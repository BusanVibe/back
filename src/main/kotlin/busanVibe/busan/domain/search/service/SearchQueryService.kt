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
    fun getSearchResult(infoType: InfoType, sort: GeneralSortType): SearchResultDTO.ListDto {
        val currentUser = AuthService().getCurrentUser()

        // 축제 + 혼잡도 검색 예외 처리
        if(infoType == InfoType.FESTIVAL && sort == GeneralSortType.CONGESTION) {
            throw ExceptionHandler(ErrorStatus.SEARCH_INVALID_CONDITION)
        }

        // 명소 타입별 조회
        val places = when (infoType) {
            InfoType.ALL -> placeRepository.findAllWithLikesAndOpenTime()
            InfoType.CAFE -> placeRepository.findAllWithLikesAndOpenTimeByType(PlaceType.CAFE)
            InfoType.RESTAURANT -> placeRepository.findAllWithLikesAndOpenTimeByType(PlaceType.RESTAURANT)
            InfoType.SIGHT -> placeRepository.findAllWithLikesAndOpenTimeByType(PlaceType.SIGHT)
            else -> emptyList()
        }

        // 축제 조회
        val festivals = when (infoType) {
            InfoType.ALL, InfoType.FESTIVAL -> festivalRepository.findAllWithFetch()
            else -> emptyList()
        }

        val placeLikeList = placeLikeRepository.findLikeByPlace(places)
        val festivalLikeList = festivalLikeRepository.findLikeByFestival(festivals)

        // 엔티티 List로 DTO List 반환
        val resultList = searchUtil.listToSearchDTO(places, festivals, sort, currentUser, placeLikeList, festivalLikeList)

        return SearchResultDTO.ListDto(sort.name, resultList)
    }

}