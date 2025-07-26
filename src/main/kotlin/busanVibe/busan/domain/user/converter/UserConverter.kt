package busanVibe.busan.domain.user.converter

import busanVibe.busan.domain.user.data.User
import busanVibe.busan.domain.user.data.dto.TokenResponseDto
import busanVibe.busan.domain.user.data.dto.UserResponseDTO
import org.springframework.stereotype.Component

@Component
class UserConverter {

    fun toLoginDto(
        user: User, isNewUser: Boolean, tokenResponseDto: TokenResponseDto
    ): UserResponseDTO.LoginDto {
        return UserResponseDTO.LoginDto(
            user.id, tokenResponseDto, user.email, isNewUser
        )
    }


}