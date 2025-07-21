package busanVibe.busan.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class UserResponseDTO {

  @Getter
  @AllArgsConstructor
  public static class loginDto {
    private Long id;
    private TokenResponseDTO tokenResponseDTO;
    private String email;
    private boolean isNewUser;
  }

}
