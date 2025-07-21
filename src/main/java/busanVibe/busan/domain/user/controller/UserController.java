package busanVibe.busan.domain.user.controller;

import busanVibe.busan.domain.user.dto.UserResponseDTO;
import busanVibe.busan.domain.user.service.UserCommandService;
import busanVibe.busan.global.apiPayload.exception.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

  private final UserCommandService userCommandService;

  @GetMapping("/oauth/kakao")
  @Operation(
      summary = "로그인 및 토큰 발급 API",
      description =
          "\"code\" 와 함께 요청 시 기존/신규 유저를 구분하고 AccessToken을 응답 헤더에 담아 반환합니다.\n"
              + "- isNewUser: false (기존 유저, DB 조회 확인됨)\n"
              + "- isNewUser: true  (신규 유저, DB에 없음)",
      responses = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
              responseCode = "200",
              description = "성공적으로 토큰 발급 및 유저 정보 반환",
              headers = {
                  @Header(
                      name = HttpHeaders.AUTHORIZATION,
                      description = "JWT access token",
                      schema = @Schema(type = "string"))
              },
              content =
              @Content(
                  mediaType = "application/json",
                  schema =
                  @Schema(
                      implementation =
                          UserResponseDTO.loginDto.class))),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
              responseCode = "400",
              description = "Invalid Parameter (예: code 누락)",
              content = @Content(mediaType = "application/json")),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
              responseCode = "500",
              description = "Internal Server Error (카카오 API 오류 또는 DB 처리 실패)",
              content = @Content(mediaType = "application/json"))
      })
  public ResponseEntity<ApiResponse<UserResponseDTO.loginDto>> callback(
      @RequestParam("code") String code
  ) {
    log.info("!login!");
    log.info("code: " + code);
    UserResponseDTO.loginDto userResponse = userCommandService.loginOrRegisterByKakao(code);
    return ResponseEntity.ok()
        .header(
            HttpHeaders.AUTHORIZATION,
            "Bearer " + userResponse.getTokenResponseDTO().getAccessToken()
        )
        .body(ApiResponse.onSuccess(userResponse));
  }


}
