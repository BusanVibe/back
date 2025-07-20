package busanVibe.busan.global.apiPayload.exception.handler;

import busanVibe.busan.global.apiPayload.code.BaseErrorCode;
import busanVibe.busan.global.apiPayload.exception.GeneralException;

public class ExceptionHandler extends GeneralException {
    public ExceptionHandler(BaseErrorCode code) {
        super(code);
    }
}
