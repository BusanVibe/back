package busanVibe.busan.domain.festival.converter

import busanVibe.busan.domain.festival.domain.Festival
import busanVibe.busan.domain.festival.dto.FestivalListResponseDTO
import busanVibe.busan.domain.festival.util.FestivalDateUtil

class FestivalConverter {

    private val festivalDateUtil: FestivalDateUtil = FestivalDateUtil()

    fun toInfoDto(festival: Festival, festivalImageMap: Map<Long, String>, userLikedFestivalIdList: Set<Long>, likeCountMap: Map<Long, Int>): FestivalListResponseDTO.FestivalInfoDto {
        val festivalId = festival.id!!
        return FestivalListResponseDTO.FestivalInfoDto(
            id = festival.id,
            name = festival.name,
            img = festivalImageMap[festivalId],
            startDate = festivalDateUtil.removeTime(festival.startDate),
            endDate = festivalDateUtil.removeTime(festival.endDate),
            address = festival.place,
            isLike = userLikedFestivalIdList.contains(festivalId),
            likeAmount = likeCountMap[festivalId] ?: 0,
        )
    }


}