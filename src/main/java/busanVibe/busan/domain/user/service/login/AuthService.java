package busanVibe.busan.domain.user.service.login;

import busanVibe.busan.domain.user.domain.User;
import busanVibe.busan.domain.user.repository.UserRepository;
import busanVibe.busan.global.apiPayload.code.status.ErrorStatus;
import busanVibe.busan.global.apiPayload.exception.handler.ExceptionHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class AuthService {

  private UserRepository userRepository;

  public User getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || authentication.getName() == null) {
      throw new ExceptionHandler(ErrorStatus.AUTHENTICATION_FAILED);
    }

    String email = authentication.getName();

    return userRepository
        .findByEmail(email)
        .orElseThrow(() -> new ExceptionHandler(ErrorStatus.USER_NOT_FOUND));
  }

  public Long getCurrentUserId() {
    return getCurrentUser().getId();
  }

  public String getCurrentUserEmail() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication.getName() == null) {
      throw new ExceptionHandler(ErrorStatus.AUTHENTICATION_FAILED);
    }
    return authentication.getName();
  }



}
