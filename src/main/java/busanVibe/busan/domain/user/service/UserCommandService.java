package busanVibe.busan.domain.user.service;

import busanVibe.busan.domain.user.converter.UserConverter;
import busanVibe.busan.domain.user.domain.User;
import busanVibe.busan.domain.user.dto.KaKaoUserInfoResponseDTO;
import busanVibe.busan.domain.user.dto.KakaoTokenResponseDTO;
import busanVibe.busan.domain.user.dto.TokenResponseDTO;
import busanVibe.busan.domain.user.dto.UserResponseDTO;
import busanVibe.busan.domain.user.repository.UserRepository;
import busanVibe.busan.global.config.security.JwtTokenProvider;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserCommandService {

  public UserResponseDTO.loginDto loginOrRegisterByKakao(String code) {

    KakaoTokenResponseDTO token = getKakaoToken(code);
    KaKaoUserInfoResponseDTO userInfo = getUserInfo(token.getAccessToken());

    String email = userInfo.getKakaoAccount().getEmail();
    String nickname = Optional.ofNullable(userInfo.getKakaoAccount())
        .map(KaKaoUserInfoResponseDTO.KakaoAccount::getProfile)
        .map(KaKaoUserInfoResponseDTO.KakaoAccount.Profile::getNickName)
        .orElse("카카오 사용자");
    String profileImageUrl = userInfo.getKakaoAccount().getProfile().getProfileImageUrl();

    TokenResponseDTO tokenResponseDTO =
        TokenResponseDTO.of(token.getAccessToken(), token.getRefreshToken());

    return isnewUser(email, nickname, profileImageUrl, tokenResponseDTO);
  }

  @Value("${spring.kakao.client-id}")
  private String clientId;

  @Value("${spring.kakao.redirect-uri}")
  private String redirectUri;

  private final WebClient kakaoTokenWebClient;
  private final WebClient kakaoUserInfoWebClient;

  private final UserRepository userRepository;
  private final UserConverter userConverter;

  private final JwtTokenProvider jwtTokenProvider;

  private KakaoTokenResponseDTO getKakaoToken(String code) {
    KakaoTokenResponseDTO kakaoTokenResponseDTO = kakaoTokenWebClient
        .post()
        .uri("/oauth/token")
        .body(
            BodyInserters.fromFormData("grant_type", "authorization_code")
                .with("client_id", clientId)
                .with("redirect_uri", redirectUri)
                .with("code", code)
        ).retrieve()
        .onStatus(
            HttpStatusCode::is4xxClientError,
            clientResponse ->
                Mono.error(new RuntimeException("Invalid Parameter")))
        .onStatus(
            HttpStatusCode::is5xxServerError,
            clientResponse ->
                Mono.error(new RuntimeException("Internal Server Error")))
        .bodyToMono(KakaoTokenResponseDTO.class)
        .block();

    log.info("[Kakao Service] Access Token ------> {}", kakaoTokenResponseDTO.getAccessToken());
    log.info(
        "[Kakao Service] Refresh Token ------> {}",
        kakaoTokenResponseDTO.getRefreshToken());
    return kakaoTokenResponseDTO;
  }

  private KaKaoUserInfoResponseDTO getUserInfo(String accessToken) {
    return kakaoUserInfoWebClient
        .get()
        .uri("/v2/user/me")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
        .retrieve()
        .bodyToMono(KaKaoUserInfoResponseDTO.class)
        .block();
  }

  /** 신규 유저인지 확인하고 가입 or 로그인 처리 */
  private UserResponseDTO.loginDto isnewUser(
      String email, String nickname, String profileImageUrl, TokenResponseDTO tokenResponseDto) {
    return userRepository
        .findByEmail(email)
        .map(
            user -> {
              log.info("기존 유저 로그인: {}", user.getEmail());
              TokenResponseDTO token = jwtTokenProvider.createToken(user);
              return userConverter.toLoginDto(user, false, token);
            })
        .orElseGet(
            () -> {
              log.info("신규 유저 회원가입: {}", email);
              User newUser = User.builder()
                  .email(email)
                  .nickname(nickname)
                  .profile_image(profileImageUrl)
                  .build();

              userRepository.save(newUser);
              TokenResponseDTO token = jwtTokenProvider.createToken(newUser);
              return userConverter.toLoginDto(newUser, true, token);
            });
  }

}
