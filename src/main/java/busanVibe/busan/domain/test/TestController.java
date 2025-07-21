package busanVibe.busan.domain.test;

import busanVibe.busan.domain.test.TestResponseDto.TestDto;
import busanVibe.busan.domain.user.service.login.AuthService;
import busanVibe.busan.global.apiPayload.exception.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import java.util.ArrayList;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

  private final TestService testService;
  private final AuthService authService;

  @GetMapping
  @Operation(summary = "test", description = "testtest")
  public ApiResponse<TestResponseDto.TestDto> testPage() {
    TestDto test = TestDto.builder()
        .title("제목1")
        .description("설명1")
        .build();
    return ApiResponse.onSuccess(test);
  }

  @GetMapping("/member")
  @Operation(summary = "유저 정보 가져오기 test")
  public String testMember(@AuthenticationPrincipal UserDetails userDetails) {
    Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
    System.out.println("authorities = " + new ArrayList<>(authorities));
    System.out.println("userDetails.getUsername() = " + userDetails.getUsername());
    System.out.println("authService.getCurrentUserEmail() = " + authService.getCurrentUserEmail());
    return "hello";
  }



}


