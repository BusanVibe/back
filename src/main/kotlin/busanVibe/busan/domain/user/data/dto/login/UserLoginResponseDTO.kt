package busanVibe.busan.domain.user.data.dto.login

class UserLoginResponseDTO {

    data class LoginDto(
        val id: Long?,
        val tokenResponseDTO: TokenResponseDto,
        val email: String?,
        val isNewUser: Boolean
    )


}