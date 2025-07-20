package busanVibe.busan.global.apiPayload.code;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
public class ErrorReasonDto {
    private HttpStatus httpStatus;

    private final boolean isSuccess;
    private final String code;
    private final String message;

    public boolean getIsSuccess() {
        return isSuccess;
    }

    public static ErrorReasonDto of(HttpStatus status, String code, String message) {
        return new ErrorReasonDto(status, false, code, message);
    }
}
