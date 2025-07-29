package busanVibe.busan.domain.user.controller

import busanVibe.busan.domain.user.data.dto.UserResponseDTO
import busanVibe.busan.domain.user.service.UserCommandService
import busanVibe.busan.global.apiPayload.exception.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "유저 관련 API", description = "로그인과 분리할수도 있습니다")
@RestController
@RequestMapping("/users")
class UserController (
    private val userCommandService: UserCommandService
){

    private val log = LoggerFactory.getLogger(UserController::class.java)

    @GetMapping("/oauth/kakao")
    fun callBack(@RequestParam("code") code: String): ResponseEntity<ApiResponse<UserResponseDTO.LoginDto>> {
        log.info("login...")
        log.info("code: $code")

        val userResponse: UserResponseDTO.LoginDto = userCommandService.loginOrRegisterByKakao(code)
        return ResponseEntity.ok()
            .header(
                HttpHeaders.AUTHORIZATION,
                "Bearer " + userResponse.tokenResponseDTO.accessToken
            )
            .body(ApiResponse.onSuccess(userResponse))

    }

    @GetMapping("/test")
    fun test(): String{
        return "hello"
    }



}