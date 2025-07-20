package busanVibe.busan.domain.test;

import lombok.Builder;
import lombok.Getter;

public class TestResponseDto {

  @Getter
  @Builder
  public static class TestDto {
    String title;
    String description;
  }


}
