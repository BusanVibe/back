package busanVibe.busan.domain.search.util

import busanVibe.busan.domain.common.dto.InfoType
import busanVibe.busan.domain.festival.domain.Festival
import busanVibe.busan.domain.festival.domain.FestivalLike
import busanVibe.busan.domain.place.domain.Place
import busanVibe.busan.domain.place.domain.PlaceLike
import busanVibe.busan.domain.place.util.PlaceRedisUtil
import busanVibe.busan.domain.search.dto.SearchResultDTO
import busanVibe.busan.domain.search.enums.GeneralSortType
import busanVibe.busan.domain.user.data.User
import org.springframework.stereotype.Component

@Component
class SearchUtil(
    private val placeRedisUtil: PlaceRedisUtil
) {

    /**
     * List<Place>와 List<Festival>로 정렬 조건에 맞는 List<SearchResultDTO.InfoDto> 반환
     */
    fun listToSearchDTO(places: List<Place>, festivals: List<Festival>, sort: GeneralSortType, currentUser: User, placeLikeList: List<PlaceLike>, festivalLikeList: List<FestivalLike>): List<SearchResultDTO.InfoDto> {

        // 축제 List -> dto List 변환
        val festivalDtoList = festivals.map { festival ->

            val festivalLikeCount = festivalLikeList.filter { it.festival == festival }.size

            SearchResultDTO.InfoDto(
                typeKr = InfoType.FESTIVAL.kr,
                typeEn = InfoType.FESTIVAL.en,
                id = festival.id,
                name = festival.name,
                address = festival.place,
                isLiked = festival.festivalLikes.any { it.user == currentUser },
                startDate = festival.startDate.toString(),
                endDate = festival.endDate.toString(),
                isEnd = festival.endDate.before(java.util.Date()),
                likeCount = festivalLikeCount
            )
        }

        // 혼잡도 List -> dto List 변환
        val placeDtoList = places.map { place ->

            val placeLikeCount = placeLikeList.filter { it.place == place }.size

            SearchResultDTO.InfoDto(
                typeKr = place.type.korean,
                typeEn = place.type.capitalEnglish,
                id = place.id,
                name = place.name,
                latitude = place.latitude?.toDouble(),
                longitude = place.longitude?.toDouble(),
                address = place.address,
                congestionLevel = placeRedisUtil.getRedisCongestion(place.id),
                isLiked = place.placeLikes.any { it.user == currentUser },
                startDate = null,
                endDate = null,
                isEnd = null,
                likeCount = placeLikeCount
            )
        }


        val dtoList = placeDtoList + festivalDtoList

        // 정렬 기준에 따라 처리
        return when (sort) {
            GeneralSortType.LIKE -> dtoList.sortedByDescending { it.likeCount }
            GeneralSortType.CONGESTION -> placeDtoList
                .filter { it.congestionLevel != null }
                .sortedBy { it.congestionLevel }
            GeneralSortType.DEFAULT -> dtoList.shuffled()
        }


    }

}