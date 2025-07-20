package busanVibe.busan.global.apiPayload.code;

public interface BaseErrorCode {
    ErrorReasonDto getReason();

    ErrorReasonDto getReasonHttpStatus();
}
