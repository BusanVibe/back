package busanVibe.busan.domain.test;

import busanVibe.busan.domain.user.service.login.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestService {

  private final AuthService authService;

  public String test1() {
    return null;
  }

}
