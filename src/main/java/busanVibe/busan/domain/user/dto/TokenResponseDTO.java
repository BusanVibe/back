package busanVibe.busan.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TokenResponseDTO {

  private final String accessToken;
  private final String refreshToken;

  public TokenResponseDTO(String accessToken, String refreshToken) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
  }

  public static TokenResponseDTO of(final String accessToken, final String refreshToken) {
    return new TokenResponseDTO(accessToken, refreshToken);
  }

}
