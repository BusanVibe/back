package busanVibe.busan.global.apiPayload.exception.handler

import busanVibe.busan.global.apiPayload.code.BaseErrorCode
import busanVibe.busan.global.apiPayload.exception.GeneralException

class ExceptionHandler(code: BaseErrorCode): GeneralException(code) {



}