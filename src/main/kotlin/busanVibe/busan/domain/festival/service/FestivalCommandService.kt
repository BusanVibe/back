package busanVibe.busan.domain.festival.service

import busanVibe.busan.domain.festival.domain.Festival
import busanVibe.busan.domain.festival.domain.FestivalLike
import busanVibe.busan.domain.festival.domain.FestivalLikeId
import busanVibe.busan.domain.festival.dto.FestivalLikeResponseDTO
import busanVibe.busan.domain.festival.repository.FestivalLikesRepository
import busanVibe.busan.domain.festival.repository.FestivalRepository
import busanVibe.busan.domain.user.data.User
import busanVibe.busan.domain.user.service.login.AuthService
import busanVibe.busan.global.apiPayload.code.status.ErrorStatus
import busanVibe.busan.global.apiPayload.exception.handler.ExceptionHandler
import jakarta.transaction.Transactional
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service

@Service
class FestivalCommandService(
    val festivalRepository: FestivalRepository,
    val festivalLikeRepository: FestivalLikesRepository,
    private val festivalLikesRepository: FestivalLikesRepository
) {

    @Transactional
    fun like(festivalId: Long?): FestivalLikeResponseDTO{

        // null 처리
        if( festivalId == null ){
            throw ExceptionHandler(ErrorStatus.FESTIVAL_ID_REQUIRED)
        }

        // 필요한 객체 생성
        val currentUser = AuthService().getCurrentUser()
        val festival: Festival = festivalRepository.findById(festivalId).orElseThrow{ ExceptionHandler(ErrorStatus.FESTIVAL_NOT_FOUND) }
        var message: String

        // 좋아요 정보 조회
        val festivalLike = festivalLikeRepository.findByFestivalAndUser(festival, currentUser)

        // 기존 좋아요 여부에 따른 처리
        if(festivalLike != null){ // 이미 좋아요 누른 경우
            cancelLike(festival, currentUser) // 좋아요 취소
            message = "좋아요 취소 성공"
        }else{ // 이전에 좋아요 누르지 않은 경우
            val festivalLikeId = FestivalLikeId(userId = currentUser.id!!, festivalId = festivalId) // ID 객체 생성

            var festivalLike: FestivalLike // 객체 미리 생성

            try {
                festivalLike =
                    FestivalLike(id = festivalLikeId, user = currentUser, festival = festival)
                festivalLikesRepository.save(festivalLike) // 좋아요 정보 저장
                message = "좋아요 성공"
            } catch (e: DataIntegrityViolationException) {
                // 동시성 문제 대비 2차로 중복 처리 - 복합키 조회하여 중복 조회
                cancelLike(festival, currentUser) // 좋아요 취소
                message = "좋아요 취소 성공"
            }
        }

        // DTO 생성 및 반환
        return FestivalLikeResponseDTO(
            success = true,
            message = message
        )

    }

    // 좋아요 취소 처리 메서드
    private fun cancelLike(festival: Festival, user: User) {
        try{
            festivalLikeRepository.deleteByFestivalAndUser(festival, user)
        }catch (e: Exception){
            throw RuntimeException(e.message, e)
        }
    }

}