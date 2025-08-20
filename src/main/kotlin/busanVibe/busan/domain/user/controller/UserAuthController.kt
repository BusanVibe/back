package busanVibe.busan.domain.user.controller

import busanVibe.busan.domain.user.data.dto.login.TokenResponseDto
import busanVibe.busan.domain.user.data.dto.login.UserLoginRequestDTO
import busanVibe.busan.domain.user.data.dto.login.UserLoginResponseDTO
import busanVibe.busan.domain.user.service.UserCommandService
import busanVibe.busan.domain.user.util.LoginRedirectUtil
import busanVibe.busan.global.apiPayload.exception.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "로그인 관련 API", description = "로그인과 분리할수도 있습니다")
@RestController
@RequestMapping("/users")
class UserAuthController (
    private val userCommandService: UserCommandService,
    private val loginRedirectUtil: LoginRedirectUtil
){

    @GetMapping("/oauth/kakao")
    @Operation(hidden = true)
    fun callBack(@RequestParam("code") code: String): ResponseEntity<Void> {
        val userResponse: UserLoginResponseDTO.LoginDto = userCommandService.loginOrRegisterByKakao(code)
        val redirectHeader = loginRedirectUtil.getRedirectHeader(userResponse)
        return ResponseEntity.status(HttpStatus.FOUND).headers(redirectHeader).build()
    }

    @PostMapping("/login/guest")
    @Operation(summary = "게스트 로그인 API", description = "1회용 계정 로그인입니다. 재로그인이 불가능합니다.")
    fun guestLogin(): ApiResponse<TokenResponseDto>{
        val userResponse: UserLoginResponseDTO.LoginDto = userCommandService.guestLogin()
        return ApiResponse.onSuccess(userResponse.tokenResponseDTO)
    }

    @PostMapping("/login/local")
    @Operation(summary = "로컬 로그인 API")
    fun localLogin(@Valid @RequestBody requestDTO: UserLoginRequestDTO.LocalLoginDto): ApiResponse<TokenResponseDto>{
        val userResponse = userCommandService.localLogin(requestDTO)
        return ApiResponse.onSuccess(userResponse.tokenResponseDTO)
    }

    @PostMapping("/signup/local")
    @Operation(summary = "로컬 회원가입 API",
        description =
            """
                email: 이메일 형식 안 지킬 시 오류 발생
                password: 4~20글자. 형식 자유.
            """
    )
    fun localSignUp(@Valid @RequestBody requestDTO: UserLoginRequestDTO.LocalSignUpDto): ResponseEntity<Void>{
        userCommandService.localSignUp(requestDTO)
        return ResponseEntity.ok().build()
    }




}