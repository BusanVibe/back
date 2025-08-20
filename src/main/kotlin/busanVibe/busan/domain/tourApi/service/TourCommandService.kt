package busanVibe.busan.domain.tourApi.service

import busanVibe.busan.domain.festival.repository.FestivalRepository
import busanVibe.busan.domain.place.domain.Place
import busanVibe.busan.domain.place.enums.PlaceType
import busanVibe.busan.domain.place.repository.PlaceJdbcRepository
import busanVibe.busan.domain.place.repository.PlaceRepository
import busanVibe.busan.domain.tourApi.dto.PlaceIntroductionItem
import busanVibe.busan.domain.tourApi.util.TourFestivalConverter
import busanVibe.busan.domain.tourApi.util.TourFestivalUtil
import busanVibe.busan.domain.tourApi.util.TourPlaceUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class TourCommandService(
    private val festivalRepository: FestivalRepository,
    private val placeRepository: PlaceRepository,
    private val placeJdbcRepository: PlaceJdbcRepository,
    private val tourFestivalUtil: TourFestivalUtil,
    private val tourPlaceUtil: TourPlaceUtil,
) {

    private val noInfo: String = "정보 없음"
    private val log: Logger = LoggerFactory.getLogger(TourCommandService::class.java)

    fun syncFestivalsFromApi() {
        val converter = TourFestivalConverter()
        val festivals = tourFestivalUtil.getFestivals().map { converter.run { it.toEntity() } }
        festivalRepository.saveAll(festivals)
    }


    @Transactional
    fun getPlace(placeType: PlaceType) {
        val apiResponse = tourPlaceUtil.getPlace(placeType).response
        val items = apiResponse.body?.items?.item

        val placeList : MutableList<Place> = mutableListOf<Place>()

        items?.forEach { apiItem ->

            val detailResponse = tourPlaceUtil.getPlaceDetail(apiItem.contentId).response
            val introResponse = tourPlaceUtil.getPlaceIntro(
                apiItem.contentId,
                apiItem.contentTypeId.toString()
            ).response

            val detailItem = detailResponse.body?.items?.item?.firstOrNull()
            val introItem = introResponse.body?.items?.item?.firstOrNull()

            val place = Place(
                contentId = apiItem.contentId,
                name = apiItem.title.orNoInfo(),
                type = placeType,
                latitude = apiItem.mapY?.toBigDecimal(),
                longitude = apiItem.mapX?.toBigDecimal(),
                address = apiItem.addr1.orNoInfo(),
                introduction = detailItem?.overview.orNoInfo(),
                phone = listOf(apiItem.tel, detailItem?.tel, getCenter(placeType, introItem))
                    .firstOrNull { !it.isNullOrBlank() }
                    .orNoInfo(),
                useTime = getUseTime(placeType, introItem).orNoInfo(),
                restDate = getRest(placeType, introItem).orNoInfo(),
                reviews = emptyList(),
                placeLikes = emptySet(),
                openTime = null,
                placeImages = mutableSetOf(),
                visitorDistribution = null,
            )


            // 이미지 추가 (firstimage, firstimage2 등 있을 수 있음)
            apiItem.firstImage?.let { place.addImage(it) }
            apiItem.firstImage2?.let { place.addImage(it) }

//             저장 (중복 방지)
//            if (!placeRepository.existsByContentId(apiItem.contentId)) {
//                placeRepository.save(place)
//            }
            placeList.add(place)
        }
//        placeJdbcRepository.saveAll(placeList)
        placeRepository.saveAll(placeList)
    }

    private fun getUseTime(placeType: PlaceType, introItem: PlaceIntroductionItem?): String =
        when (placeType) {
            PlaceType.ALL -> introItem?.useTime
//            PlaceType.CAFE -> introItem?.openTimeFood
            PlaceType.RESTAURANT -> introItem?.openTimeFood
            PlaceType.SIGHT -> introItem?.useTime
            PlaceType.CULTURE -> introItem?.useTimeCulture
        }.orNoInfo()

    private fun getRest(placeType: PlaceType, introItem: PlaceIntroductionItem?): String =
        when (placeType) {
            PlaceType.ALL -> introItem?.restDate
//            PlaceType.CAFE -> introItem?.restDateFood
            PlaceType.RESTAURANT -> introItem?.restDateFood
            PlaceType.SIGHT -> introItem?.restDate
            PlaceType.CULTURE -> introItem?.restDateCulture
        }.orNoInfo()

    private fun getCenter(placeType: PlaceType, introItem: PlaceIntroductionItem?): String =
        when (placeType) {
            PlaceType.ALL -> introItem?.infoCenter
//            PlaceType.CAFE -> introItem?.infoCenterFood
            PlaceType.RESTAURANT -> introItem?.infoCenterFood
            PlaceType.SIGHT -> introItem?.infoCenter
            PlaceType.CULTURE -> introItem?.infoCenterCulture
        }.orNoInfo()


    private fun String?.orNoInfo(): String = if (this.isNullOrBlank()) noInfo else this
    private fun BigDecimal?.orZero(): BigDecimal = this ?: BigDecimal.ZERO

}
