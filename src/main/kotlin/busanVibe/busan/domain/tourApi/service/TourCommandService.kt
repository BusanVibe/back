package busanVibe.busan.domain.tourApi.service

import busanVibe.busan.domain.festival.repository.FestivalRepository
import busanVibe.busan.domain.place.domain.Place
import busanVibe.busan.domain.place.domain.VisitorDistribution
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
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.random.Random

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

    fun syncFestivalsFromApi(pageSize: Int, pageNum: Int) {
        val converter = TourFestivalConverter()
        val festivals = tourFestivalUtil.getFestivals(pageSize, pageNum)
            .map { converter.run { it.toEntity() } }
            .filter { it.startDate != null && it.endDate != null } // 시작일 혹은 종료일 정보 없을 시 해당 정보 저장X
        festivalRepository.saveAll(festivals)
    }


//    @Transactional
    fun getPlace(placeType: PlaceType, pageSize: Int, pageNum: Int) {
        val apiResponse = tourPlaceUtil.getPlace(placeType, pageSize, pageNum).response
        val items = apiResponse.body?.items?.item

        val placeList : MutableList<Place> = mutableListOf<Place>()

        items?.forEach { apiItem ->

            val detailResponse = tourPlaceUtil.getPlaceDetail(apiItem.contentId).response
            val introResponse = tourPlaceUtil.getPlaceIntro(
                apiItem.contentId,
                apiItem.contentTypeId.toString()
            ).response
            val placeImageResponse = tourPlaceUtil.getImages(apiItem.contentId)

            val detailItem = detailResponse.body?.items?.item?.firstOrNull()
            val introItem = introResponse.body?.items?.item?.firstOrNull()
            val placeImages = placeImageResponse.response.body?.items?.item
                ?.mapNotNull { it.originImgUrl }
                ?.toList() ?: emptyList()

            val place = Place(
                contentId = apiItem.contentId,
                name = apiItem.title.orNoInfo().removeTag(),
                type = PlaceType.fromTourApiTypeId(apiItem.contentTypeId),
                latitude = apiItem.mapY?.toBigDecimal()?.setScale(4, RoundingMode.HALF_UP),
                longitude = apiItem.mapX?.toBigDecimal()?.setScale(4, RoundingMode.HALF_UP),
                address = apiItem.addr1.orNoInfo().removeTag(),
                introduction = detailItem?.overview.orNoInfo().removeTag(),
                phone = listOf(apiItem.tel, detailItem?.tel, getCenter(placeType, introItem))
                    .firstOrNull { !it.isNullOrBlank() }
                    .orNoInfo().removeTag(),
                useTime = getUseTime(placeType, introItem).orNoInfo().removeTag(),
                restDate = getRest(placeType, introItem).orNoInfo().removeTag(),
                placeLikes = emptySet(),
                placeImages = mutableSetOf(),
                visitorDistribution = getRandomVisitorDistribution(),
            )


            // 이미지 추가
            apiItem.firstImage?.let { place.addImage(it) } // 소개 정보 조회에서 가져온 이미지
            placeImages.forEach { place.addImage(it) } // 이미지 정보 조회에서 가져온 이미지

//             저장 (중복 방지)
//            if (!placeRepository.existsByContentId(apiItem.contentId)) {
//                placeRepository.save(place)
//            }

            // 이미지 있는 것들만 저장
            if(place.placeImages.isNotEmpty()) {
                placeList.add(place)
            }
        }
//        placeJdbcRepository.saveAll(placeList)
//        placeRepository.saveAll(placeList)
        saveAllPlaces(placeList)
    }

    private fun getUseTime(placeType: PlaceType, introItem: PlaceIntroductionItem?): String =
        when (placeType) {
            PlaceType.ALL -> introItem?.useTime
            PlaceType.RESTAURANT -> introItem?.openTimeFood
            PlaceType.SIGHT -> introItem?.useTime
            PlaceType.CULTURE -> introItem?.useTimeCulture
        }.orNoInfo()

    private fun getRest(placeType: PlaceType, introItem: PlaceIntroductionItem?): String =
        when (placeType) {
            PlaceType.ALL -> introItem?.restDate
            PlaceType.RESTAURANT -> introItem?.restDateFood
            PlaceType.SIGHT -> introItem?.restDate
            PlaceType.CULTURE -> introItem?.restDateCulture
        }.orNoInfo()

    private fun getCenter(placeType: PlaceType, introItem: PlaceIntroductionItem?): String =
        when (placeType) {
            PlaceType.ALL -> introItem?.infoCenter
            PlaceType.RESTAURANT -> introItem?.infoCenterFood
            PlaceType.SIGHT -> introItem?.infoCenter
            PlaceType.CULTURE -> introItem?.infoCenterCulture
        }.orNoInfo()

    // 문자열 속 태그 제거 메서드
    private fun String.removeTag(): String{
        return this.replace(Regex("<[^>]*>"), "")
    }

    private fun getRandomVisitorDistribution(): VisitorDistribution {
        // 그룹별 가중치 설정
        val weights = mapOf(
            "m1020" to 3,
            "f1020" to 3,
            "m3040" to 3,
            "f3040" to 3,
            "m5060" to 2,
            "f5060" to 2,
            "m70"   to 1,
            "f70"   to 1,
        )

        val totalWeight = weights.values.sum()

        // 비율대로 난수 생성
        val raw = weights.mapValues { (_, w) -> Random.nextDouble() * w }

        val rawSum = raw.values.sum()
        val scaled = raw.mapValues { (_, v) -> (v / rawSum * 100).toInt() }

        // 반올림으로 인해 총합이 100이 안될 수 있으므로 보정
        val diff = 100 - scaled.values.sum()
        val result = scaled.toMutableMap()
        if (diff != 0) {
            val adjustKey = result.keys.random()
            result[adjustKey] = (result[adjustKey] ?: 0) + diff
        }

        return VisitorDistribution(
            m1020 = result["m1020"] ?: 0,
            f1020 = result["f1020"] ?: 0,
            m3040 = result["m3040"] ?: 0,
            f3040 = result["f3040"] ?: 0,
            m5060 = result["m5060"] ?: 0,
            f5060 = result["f5060"] ?: 0,
            m70   = result["m70"] ?: 0,
            f70   = result["f70"] ?: 0,
        )
    }


    private fun String?.orNoInfo(): String = if (this.isNullOrBlank()) noInfo else this
    private fun BigDecimal?.orZero(): BigDecimal = this ?: BigDecimal.ZERO

    fun saveAllPlaces(placeList: List<Place>) {
        placeList.forEach { place ->
            try {
                saveOne(place)   // 별도 트랜잭션
            } catch (e: Exception) {
                log.error("저장 실패: ${place.contentId}, 예외 메시지: ${e.message}")
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun saveOne(place: Place) {
        placeRepository.save(place)
    }

}

