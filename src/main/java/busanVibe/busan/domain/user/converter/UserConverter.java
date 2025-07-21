package busanVibe.busan.domain.user.converter;

import busanVibe.busan.domain.user.domain.User;
import busanVibe.busan.domain.user.dto.TokenResponseDTO;
import busanVibe.busan.domain.user.dto.UserResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class UserConverter {

  public UserResponseDTO.loginDto toLoginDto(
      User user, boolean isNewUser, TokenResponseDTO tokenResponseDTO) {

    return new UserResponseDTO.loginDto(
        user.getId(), tokenResponseDTO, user.getEmail(), isNewUser
    );
  }

}
