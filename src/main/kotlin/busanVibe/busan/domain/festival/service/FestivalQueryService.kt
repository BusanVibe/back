package busanVibe.busan.domain.festival.service

import busanVibe.busan.domain.festival.converter.FestivalConverter
import busanVibe.busan.domain.festival.domain.Festival
import busanVibe.busan.domain.festival.domain.FestivalLike
import busanVibe.busan.domain.festival.dto.FestivalDetailsDTO
import busanVibe.busan.domain.festival.dto.FestivalListResponseDTO
import busanVibe.busan.domain.festival.enums.FestivalSortType
import busanVibe.busan.domain.festival.enums.FestivalStatus
import busanVibe.busan.domain.festival.repository.FestivalImageRepository
import busanVibe.busan.domain.festival.repository.FestivalLikesRepository
import busanVibe.busan.domain.festival.repository.FestivalRepository
import busanVibe.busan.domain.festival.util.FestivalDateUtil
import busanVibe.busan.domain.user.data.User
import busanVibe.busan.domain.user.service.login.AuthService
import busanVibe.busan.global.apiPayload.code.status.ErrorStatus
import busanVibe.busan.global.apiPayload.exception.handler.ExceptionHandler
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.xml.sax.ErrorHandler
import java.time.LocalDate

@Service
class FestivalQueryService(
    private val festivalRepository: FestivalRepository,
    private val festivalImageRepository: FestivalImageRepository,
    private val festivalLikesRepository: FestivalLikesRepository
) {

    val log = LoggerFactory.getLogger(FestivalQueryService::class.java)

    @Transactional(readOnly = true)
    fun getFestivalList(sort: FestivalSortType?, status: FestivalStatus?): FestivalListResponseDTO.ListDto{

        val currentUser: User = AuthService().getCurrentUser()

        // Festival -> id, name, starDate, endDate, place
        // FestivalImage -> imgUrl
        // FestivalLike -> 있는지 조회

        // 입력받은 파라미터 null일 경우 기본값 설정
        val sort = sort ?: FestivalSortType.DEFAULT
        val status = status ?: FestivalStatus.ALL


        // 축제 리스트 조회
        val festivals = festivalRepository.findAll()
        val today = LocalDate.now()
        val festivalList = festivals.filter { festival ->
            val start = festival.startDate
            val end = festival.endDate

            when (status) {
                FestivalStatus.ALL -> true
                FestivalStatus.UPCOMING -> start != null && today.isBefore(start)
                FestivalStatus.IN_PROGRESS -> start != null && end != null &&
                        (today.isEqual(start) || today.isEqual(end) || (today.isAfter(start) && today.isBefore(end)))
                FestivalStatus.COMPLETE -> end != null && today.isAfter(end)
            }
        }

//        val festivalList: List<Festival> = when(status){
//            FestivalStatus.ALL -> festivalRepository.findAll()
//            else -> festivalRepository.getFestivalList(status)
//        }

//        val festivalIdList: List<Long> = festivalList.mapNotNull { it.id }

        // 좋아요 리스트 조회
        val festivalLikeList: List<FestivalLike> = festivalLikesRepository.findLikeByFestival(festivalList)

        // 좋아요 수 계산
        val likeCountMap: Map<Long, Int> = festivalLikeList
            .groupingBy { it.festival.id!! }
            .eachCount()

        // 사용자의 좋아요 여부를 판단하기 위한 Set
        val userLikedFestivalIdList: Set<Long> = festivalLikeList
            .filter { it.user.id == currentUser.id }
            .map { it.festival.id!! }
            .toSet()

        // 축제 이미지 목록 조회
        val festivalImageMap: Map<Long, String> = festivalImageRepository.findAllByFestivalIn(festivalList)
            .associateBy ( { it.festival.id!!},  { it.imgUrl})


        // DTO 변환
        val festivalConverter = FestivalConverter()
        val dtoList: List<FestivalListResponseDTO.FestivalInfoDto> = festivalList.map{
            festival -> festivalConverter.toInfoDto(festival, festivalImageMap,  userLikedFestivalIdList, likeCountMap)
        }

        // 정렬 처리
        val sortedList = when(sort){
            FestivalSortType.DEFAULT -> dtoList
            FestivalSortType.START -> dtoList.sortedBy { it.startDate }
            FestivalSortType.END -> dtoList.sortedBy { it.endDate }
            FestivalSortType.LIKE -> dtoList.sortedByDescending { it.likeAmount }
        }

        return FestivalListResponseDTO.ListDto(sortedList)
    }

    fun getFestivalDetails(festivalId: Long):FestivalDetailsDTO.DetailDto{

        // Festival -> id, name, startDate, endDate, place, introduction
        // FestivalLike -> count, isLike
        // FestivalImage -> img

        // user 추출
        val currentUser: User = AuthService().getCurrentUser()

        // dateUtil
        val festivalDateUtil: FestivalDateUtil = FestivalDateUtil()

        // 축제 조회
        val festival: Festival? = festivalRepository.findByIdWithLikesAndImages(festivalId)

        // 검증
        festival ?: throw ExceptionHandler(ErrorStatus.FESTIVAL_NOT_FOUND)

        // 이미지 조회
        val imgUrlSet: Set<String> = festival.festivalImages
            .map { it.imgUrl }
            .toSet()

        // 좋아요 조회
        val likeList: Set<FestivalLike> = festival.festivalLikes

        return FestivalDetailsDTO.DetailDto(
            id = festivalId,
            img = imgUrlSet,
            name = festival.name,
            likeAmount = likeList.size,
            isLike = likeList.any { it.user.id == currentUser.id },
            startDate = festivalDateUtil.removeTime(festival.startDate),
            endDate = festivalDateUtil.removeTime(festival.endDate),
            address = festival.place,
            fee = festival.fee,
            siteUrl = festival.siteUrl,
            introduce = festival.introduction,
            phone = festival.phone,
        )
    }

}