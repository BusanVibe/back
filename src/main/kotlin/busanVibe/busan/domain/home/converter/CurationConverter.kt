package busanVibe.busan.domain.home.converter

import busanVibe.busan.domain.common.dto.InfoType
import busanVibe.busan.domain.festival.domain.Festival
import busanVibe.busan.domain.festival.util.FestivalDateUtil
import busanVibe.busan.domain.home.dto.HomeResponseDTO
import busanVibe.busan.domain.place.domain.Place

class CurationConverter {

    private val festivalDateUtil = FestivalDateUtil()

    fun placeToDto(place: Place) = HomeResponseDTO.CurationInfo(
        id = place.id,
        name = place.name,
        duration = place.useTime,
        typeKr = place.type.korean,
        typeEn = place.type.capitalEnglish,
        imgUrl = place.placeImages.first().imgUrl
    )

    fun festivalToDto(festival: Festival) =  HomeResponseDTO.CurationInfo(
        id = festival.id,
        name = festival.name,
        duration = "${festivalDateUtil.removeTime(festival.startDate)} ~ ${festivalDateUtil.removeTime(festival.endDate)}",
        typeKr = InfoType.FESTIVAL.kr,
        typeEn = InfoType.FESTIVAL.en,
        imgUrl = festival.festivalImages.first().imgUrl
    )

    fun placeListToDto(placeList: List<Place>): HomeResponseDTO.CurationList {
        val placeDtoList = placeList.map { placeToDto(it) }
        return HomeResponseDTO.CurationList(placeDtoList)
    }

    fun festivalListToDto(festivalList: List<Festival>): HomeResponseDTO.CurationList {
        val festivalDtoList = festivalList.map { festivalToDto(it) }
        return HomeResponseDTO.CurationList(festivalDtoList)
    }


}