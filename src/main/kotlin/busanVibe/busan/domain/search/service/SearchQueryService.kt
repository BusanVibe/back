package busanVibe.busan.domain.search.service

import busanVibe.busan.domain.common.dto.InfoType
import busanVibe.busan.domain.festival.repository.FestivalRepository
import busanVibe.busan.domain.place.enums.PlaceType
import busanVibe.busan.domain.place.repository.PlaceRepository
import busanVibe.busan.domain.place.service.PlaceRedisUtil
import busanVibe.busan.domain.search.dto.SearchResultDTO
import busanVibe.busan.domain.search.enums.GeneralSortType
import busanVibe.busan.domain.user.service.login.AuthService
import busanVibe.busan.global.apiPayload.code.status.ErrorStatus
import busanVibe.busan.global.apiPayload.exception.handler.ExceptionHandler
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SearchQueryService(
    private val placeRepository: PlaceRepository,
    private val festivalRepository: FestivalRepository,
    private val placeRedisUtil: PlaceRedisUtil
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

        // 축제 List -> dto List 변환
        val festivalDtoList = festivals.map { festival ->
            SearchResultDTO.InfoDto(
                typeKr = InfoType.FESTIVAL.kr,
                typeEn = InfoType.FESTIVAL.en,
                id = festival.id,
                name = festival.name,
                region = festival.place,
                isLiked = festival.festivalLikes.any { it.user == currentUser },
                startDate = festival.startDate.toString(),
                endDate = festival.endDate.toString(),
                isEnd = festival.endDate.before(java.util.Date())
            )
        }

        // 혼잡도 List -> dto List 변환
        val placeDtoList = places.map { place ->
            SearchResultDTO.InfoDto(
                typeKr = place.type.korean,
                typeEn = place.type.capitalEnglish,
                id = place.id,
                name = place.name,
                latitude = place.latitude.toDouble(),
                longitude = place.longitude.toDouble(),
                region = place.address,
                congestionLevel = placeRedisUtil.getRedisCongestion(place.id),
                isLiked = place.placeLikes.any { it.user == currentUser },
                startDate = null,
                endDate = null,
                isEnd = null
            )
        }

        // 정렬 기준에 따라 처리
        val resultList: List<SearchResultDTO.InfoDto> = when (sort) {
            GeneralSortType.LIKE -> {
                (placeDtoList + festivalDtoList).sortedByDescending { item ->
                    // 좋아요 수 기준 정렬
                    when (item.typeEn) {
                        InfoType.FESTIVAL.en -> festivals.find { it.id == item.id }?.festivalLikes?.size ?: 0
                        else -> places.find { it.id == item.id }?.placeLikes?.size ?: 0
                    }
                }
            }

            GeneralSortType.CONGESTION -> {
                // 혼잡도 정렬은 명소만 해당
                placeDtoList
                    .filter { it.congestionLevel != null }
                    .sortedBy { it.congestionLevel }
            }

            GeneralSortType.DEFAULT -> {
                // 기본 정렬은 그대로
                placeDtoList + festivalDtoList
            }
        }

        // 반환
        return SearchResultDTO.ListDto(resultList)
    }



}