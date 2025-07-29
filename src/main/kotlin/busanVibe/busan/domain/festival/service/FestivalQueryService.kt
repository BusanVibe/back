package busanVibe.busan.domain.festival.service

import busanVibe.busan.domain.festival.converter.FestivalConverter
import busanVibe.busan.domain.festival.domain.Festival
import busanVibe.busan.domain.festival.domain.FestivalLike
import busanVibe.busan.domain.festival.dto.FestivalListResponseDTO
import busanVibe.busan.domain.festival.enums.FestivalSortType
import busanVibe.busan.domain.festival.enums.FestivalStatus
import busanVibe.busan.domain.festival.repository.FestivalImageRepository
import busanVibe.busan.domain.festival.repository.FestivalLikesRepository
import busanVibe.busan.domain.festival.repository.FestivalRepository
import busanVibe.busan.domain.user.data.User
import busanVibe.busan.domain.user.service.login.AuthService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FestivalQueryService(
    private val festivalRepository: FestivalRepository,
    private val festivalImageRepository: FestivalImageRepository,
    private val festivalLikesRepository: FestivalLikesRepository
) {

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
        val festivalList: List<Festival> = when(status){
            FestivalStatus.ALL -> festivalRepository.findAll()
            else -> festivalRepository.getFestivalList(status)
        }

//        val festivalIdList: List<Long> = festivalList.mapNotNull { it.id }

        // 좋아요 리스트 조회
        val festivalLikeList: List<FestivalLike> = festivalLikesRepository.findAllByFestivalIn(festivalList)

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
            FestivalSortType.LIKE -> dtoList.sortedByDescending { it.likeCount }
        }

        return FestivalListResponseDTO.ListDto(sortedList)
    }

}