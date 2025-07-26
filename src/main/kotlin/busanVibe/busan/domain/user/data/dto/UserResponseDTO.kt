package busanVibe.busan.domain.user.data.dto

class UserResponseDTO {

    companion object class LoginDto(
        val id: Long?,
        val tokenResponseDTO: TokenResponseDto,
        val email: String?,
        val isNewUser: Boolean
    )


}