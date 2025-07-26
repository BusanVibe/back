package busanVibe.busan.global.apiPayload.code

interface BaseCode {

    fun getReason(): ReasonDTO
    fun getReasonHttpStatus(): ReasonDTO

}