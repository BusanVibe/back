package busanVibe.busan.domain.place.service

import busanVibe.busan.domain.place.domain.Place
import busanVibe.busan.domain.place.domain.PlaceLike
import busanVibe.busan.domain.place.domain.PlaceLikeId
import busanVibe.busan.domain.place.dto.PlaceResponseDTO
import busanVibe.busan.domain.place.repository.PlaceLikeRepository
import busanVibe.busan.domain.place.repository.PlaceRepository
import busanVibe.busan.domain.user.data.User
import busanVibe.busan.domain.user.service.login.AuthService
import busanVibe.busan.global.apiPayload.code.status.ErrorStatus
import busanVibe.busan.global.apiPayload.exception.handler.ExceptionHandler
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PlaceCommandService(
    private val placeRepository: PlaceRepository,
    private val placeLikeRepository: PlaceLikeRepository,
) {

    @Transactional
    fun like(placeId: Long?): PlaceResponseDTO.LikeDto{

        // null 처리
        if (placeId == null) {
            throw ExceptionHandler(ErrorStatus.PLACE_ID_REQUIRED)
        }

        // 필요한 객체 생성
        val currentUser = AuthService().getCurrentUser()
        val place: Place= placeRepository.findById(placeId).orElseThrow{ ExceptionHandler(ErrorStatus.PLACE_NOT_FOUND) }
        var message: String

        // 좋아요 정보 조회
        val placeLike = placeLikeRepository.findByPlaceAndUser(place, currentUser)

        // 기존 좋아요 여부에 따른 처리
        if (placeLike != null) { // 이미 좋아요 누른 경우 ( 데이터 조회에 따른 중복 감지 )
            cancelLike(place, currentUser) // 좋아요 취소
            message = "좋아요 취소 성공"
        }else{ // 이전에 좋아요를 누르지 않은 경우

            val placeLikeId = PlaceLikeId(userId = currentUser.id!!, placeId = placeId) // ID 객체 생성 ( 복합키 )

            var placeLike: PlaceLike // 객체 미리 선언

            try{
                placeLike = PlaceLike(id= placeLikeId, user = currentUser, place = place)
                placeLikeRepository.save(placeLike) // 좋아요 저장
                message = "좋아요 성공"
            }catch (e: DataIntegrityViolationException){
                // 동시성 문제 생각하여 2차로 중복 처리 - 복합키 조회하여 중복 조회
                cancelLike(place, currentUser) // 좋아요 취소
                message = "좋아요 취소 성공"
            }
        }

        // DTO 생성 및 반환
        return PlaceResponseDTO.LikeDto(
            success = true,
            message = message
        )

    }

    // 좋아요 취소 처리
    private fun cancelLike(place: Place, user: User){
        try {
            placeLikeRepository.deleteByPlaceAndUser(place, user)
        } catch (e: Exception){
            throw RuntimeException(e.message, e)
        }
    }


}