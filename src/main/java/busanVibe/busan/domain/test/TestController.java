package busanVibe.busan.domain.test;

import busanVibe.busan.domain.test.TestResponseDto.TestDto;
import busanVibe.busan.global.apiPayload.exception.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

  @GetMapping
  @Operation(summary = "test", description = "testtest")
  public ApiResponse<TestResponseDto.TestDto> testPage() {
    TestDto test = TestDto.builder()
        .title("제목1")
        .description("설명1")
        .build();
    return ApiResponse.onSuccess(test);
  }



}


