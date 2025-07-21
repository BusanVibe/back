package busanVibe.busan.domain.test;

import busanVibe.busan.domain.user.domain.User;
import busanVibe.busan.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TestService {

  private final UserRepository userRepository;

  @Transactional(readOnly = true)
  public String userInfoTest(String email) {

    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("존재하지 않는 이메일"));
    return "email: " + user.getEmail() + "\nname: " + user.getNickname() + "\nimg: " + user.getProfile_image();
  }

}
