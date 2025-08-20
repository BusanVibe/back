package busanVibe.busan.domain.user.service

import busanVibe.busan.domain.common.dto.InfoType
import busanVibe.busan.domain.festival.repository.FestivalLikesRepository
import busanVibe.busan.domain.festival.repository.FestivalRepository
import busanVibe.busan.domain.place.repository.PlaceLikeRepository
import busanVibe.busan.domain.place.repository.PlaceRepository
import busanVibe.busan.domain.search.dto.SearchResultDTO
import busanVibe.busan.domain.search.enums.GeneralSortType
import busanVibe.busan.domain.search.util.SearchUtil
import busanVibe.busan.domain.user.data.dto.UserResponseDTO
import busanVibe.busan.domain.user.service.login.AuthService
import busanVibe.busan.global.apiPayload.code.status.ErrorStatus
import busanVibe.busan.global.apiPayload.exception.GeneralException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserQueryService(
    private val placeRepository: PlaceRepository,
    private val festivalRepository: FestivalRepository,
    private val placeLikeRepository: PlaceLikeRepository,
    private val festivalLikeRepository: FestivalLikesRepository,
    private val searchUtil: SearchUtil,
) {

    // 마이페이지 기본 정보 조회
    @Transactional(readOnly = true)
    fun getMyPageInfo(): UserResponseDTO.MyPageDto{
        val user = AuthService().getCurrentUser()
        return UserResponseDTO.MyPageDto(
            nickname = user.nickname,
            email = user.email,
            userImageUrl = user.profileImageUrl
        )
    }

    // 유저 좋아요 목록 정보 조회
    @Transactional(readOnly = true)
    fun getMyLikes(infoType: InfoType, sort: GeneralSortType): SearchResultDTO.ListDto{

        val user = AuthService().getCurrentUser()

        // 검색조건 검사
        if(sort == GeneralSortType.CONGESTION){ // 좋아요 정보 조회에서는 혼잡도 정렬은 제외
            throw GeneralException(ErrorStatus.SEARCH_INVALID_CONDITION)
        }

        // 명소 조회
        val places = when(infoType){
            InfoType.ALL -> placeRepository.findLikePlace(user)
            InfoType.RESTAURANT, InfoType.SIGHT -> placeRepository.findLikePlaceByType(
                infoType.placeType?: throw GeneralException(ErrorStatus.SEARCH_INVALID_CONDITION),
                user
             )
            else -> emptyList()
        }

        // 축제 조회
        val festivals = when (infoType){
            InfoType.ALL, InfoType.FESTIVAL -> festivalRepository.findLikeFestivals(user)
            else -> emptyList()
        }

        val placeLikeList = placeLikeRepository.findLikeByPlace(places)
        val festivalLikeList = festivalLikeRepository.findLikeByFestival(festivals)

        // 엔티티 List로 DTO List 반환
        val resultList = searchUtil.listToSearchDTO(places, festivals, sort, user, placeLikeList, festivalLikeList)

        return SearchResultDTO.ListDto(sort.name, resultList)
    }

}