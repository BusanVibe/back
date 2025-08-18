package busanVibe.busan.domain.user.converter

import busanVibe.busan.domain.user.data.User
import busanVibe.busan.domain.user.data.dto.login.TokenResponseDto
import busanVibe.busan.domain.user.data.dto.login.UserLoginResponseDTO
import org.springframework.stereotype.Component

@Component
class UserConverter {

    fun toLoginDto(
        user: User, isNewUser: Boolean, tokenResponseDto: TokenResponseDto
    ): UserLoginResponseDTO.LoginDto {
        return UserLoginResponseDTO.LoginDto(
            user.id, tokenResponseDto, user.email, isNewUser
        )
    }


}