package busanVibe.busan.domain.test;

import busanVibe.busan.domain.user.service.login.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

  private final TestService testService;
  private final AuthService authService;

  @GetMapping("/member")
  @Operation(summary = "member info test", description = "테스트용 유저 정보 조회")
  public String testPage() {
    String email = authService.getCurrentUserEmail();
    return testService.userInfoTest(email);
  }

}


