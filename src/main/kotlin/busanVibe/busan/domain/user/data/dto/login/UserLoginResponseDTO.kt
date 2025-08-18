package busanVibe.busan.domain.user.data.dto.login

class UserLoginResponseDTO {

    companion object class LoginDto(
        val id: Long?,
        val tokenResponseDTO: TokenResponseDto,
        val email: String?,
        val isNewUser: Boolean
    )


}