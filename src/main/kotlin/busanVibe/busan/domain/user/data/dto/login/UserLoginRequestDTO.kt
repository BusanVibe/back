package busanVibe.busan.domain.user.data.dto.login

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

class UserLoginRequestDTO {

    data class LocalSignUpDto(
        @field:NotBlank(message = "이메일은 필수 입력값입니다.")
        @field:Email(message = "올바르지 않은 이메일 형식입니다.")
        val email: String,

        @field:NotBlank(message = "비밀번호는 필수입니다.")
        @field:Size(min = 4, max = 20, message = "비밀번호는 4자 이상 20자 이하로 입력해야 합니다.")
        val password: String
    )

    data class LocalLoginDto(
        @field:NotBlank(message = "이메일은 필수 입력값입니다.")
        @field:Email(message = "올바르지 않은 이메일 형식입니다.")
        val email: String,

        @field:NotBlank(message = "비밀번호는 필수입니다.")
        val password: String

    )

}