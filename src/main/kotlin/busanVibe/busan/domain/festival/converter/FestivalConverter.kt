package busanVibe.busan.domain.festival.converter

import busanVibe.busan.domain.festival.domain.Festival
import busanVibe.busan.domain.festival.dto.FestivalListResponseDTO
import java.text.SimpleDateFormat
import java.util.Date

class FestivalConverter {

    fun toInfoDto(festival: Festival, festivalImageMap: Map<Long, String>, userLikedFestivalIdList: Set<Long>, likeCountMap: Map<Long, Int>): FestivalListResponseDTO.FestivalInfoDto {
        val festivalId = festival.id!!
        return FestivalListResponseDTO.FestivalInfoDto(
            id = festival.id,
            name = festival.name,
            img = festivalImageMap[festivalId],
            startDate = convertFestivalDate(festival.startDate),
            endDate = convertFestivalDate(festival.endDate),
            address = festival.place,
            isLike = userLikedFestivalIdList.contains(festivalId),
            likeCount = likeCountMap[festivalId] ?: 0,
        )
    }

    public fun convertFestivalDate(date: Date): String {
        val formatter: SimpleDateFormat = SimpleDateFormat("yyyy.MM.dd")
        return formatter.format(date)
    }

}