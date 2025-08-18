package busanVibe.busan.domain.user.service

import busanVibe.busan.domain.user.data.dto.UserResponseDTO
import busanVibe.busan.domain.user.service.login.AuthService
import org.springframework.stereotype.Service

@Service
class UserQueryService {

    fun getMyPageInfo(): UserResponseDTO.MyPageDto{
        val user = AuthService().getCurrentUser()
        return UserResponseDTO.MyPageDto(
            nickname = user.nickname,
            email = user.email,
            userImageUrl = user.profileImageUrl
        )
    }

}