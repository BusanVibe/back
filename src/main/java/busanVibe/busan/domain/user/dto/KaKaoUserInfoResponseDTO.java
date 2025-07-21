package busanVibe.busan.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KaKaoUserInfoResponseDTO {

  @JsonProperty("id")
  private Long id;

  @JsonProperty("kakao_account")
  private KakaoAccount kakaoAccount;

  @Getter
  @NoArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class KakaoAccount {

    @JsonProperty("email")
    private String email;

    @JsonProperty("profile")
    private Profile profile;

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Profile {

      @JsonProperty("nickname")
      private String nickName;

      @JsonProperty("profile_image_url")
      private String profileImageUrl;
    }
  }

}
