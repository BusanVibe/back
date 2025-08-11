package busanVibe.busan.domain.user.controller

import busanVibe.busan.domain.user.data.dto.UserResponseDTO
import busanVibe.busan.domain.user.service.UserCommandService
import busanVibe.busan.domain.user.util.LoginRedirectUtil
import busanVibe.busan.global.apiPayload.exception.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.net.URLEncoder

@Tag(name = "유저 관련 API", description = "로그인과 분리할수도 있습니다")
@RestController
@RequestMapping("/users")
class UserController (
    private val userCommandService: UserCommandService,
    private val loginRedirectUtil: LoginRedirectUtil
){

    private val log = LoggerFactory.getLogger(UserController::class.java)

    @GetMapping("/oauth/kakao")
    fun callBack(@RequestParam("code") code: String): ResponseEntity<Void> {
        val userResponse: UserResponseDTO.LoginDto = userCommandService.loginOrRegisterByKakao(code)
        val redirectHeader = loginRedirectUtil.getRedirectHeader(userResponse)
        return ResponseEntity.status(HttpStatus.FOUND).headers(redirectHeader).build()

    }

    @GetMapping("/test")
    fun test(): String{
        return "hello"
    }



}