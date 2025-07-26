package busanVibe.busan.global.apiPayload.code

interface BaseErrorCode{

    fun getReason(): ErrorReasonDTO

    fun getReasonHttpStatus(): ErrorReasonDTO

}