package busanVibe.busan.domain.tourApi.util

import busanVibe.busan.domain.festival.domain.Festival
import busanVibe.busan.domain.festival.domain.FestivalImage
import busanVibe.busan.domain.festival.enums.FestivalStatus
import busanVibe.busan.domain.tourApi.dto.FestivalItem
import java.time.LocalDate
import java.util.Date
import java.util.regex.Pattern

class TourFestivalConverter {

    fun FestivalItem.toEntity(): Festival {
        var startDate = parseStartDate(USAGE_DAY_WEEK_AND_TIME)
        var endDate = parseEndDate(USAGE_DAY_WEEK_AND_TIME)

        // 시작일 <-> 종료일 뒤바뀌어있으면 정상으로 바꾸어 저장하도록 수정
        if(startDate?.isAfter(endDate) == true) {
            val tmp = startDate
            startDate = endDate
            endDate = tmp
        }

        val festival = Festival(
            name = MAIN_TITLE ?: "이름없음",
            startDate = startDate?: LocalDate.now(),
            endDate = endDate?: LocalDate.now(),
            place = MAIN_PLACE ?: "장소없음",
            introduction = ITEMCNTNTS?.removeTag() ?: "",
            fee = USAGE_AMOUNT?: "정보없음",
            phone = CNTCT_TEL ?: "정보없음",
            siteUrl = HOMEPAGE_URL ?: "정보없음",
            status = getStatus(USAGE_DAY_WEEK_AND_TIME?:""),
            festivalLikes = emptySet(),
            contentId = UC_SEQ
        )

        println("MAIN_IMG_NORMAL = ${MAIN_IMG_NORMAL}")
        // 이미지 추가
        MAIN_IMG_NORMAL?.let {
            festival.addFestivalImage(FestivalImage(imgUrl = it, festival = festival))
        }
        MAIN_IMG_THUMB?.let {
            festival.addFestivalImage(FestivalImage(imgUrl = it, festival = festival))
        }

        return festival
    }

    fun parseStartDate(dateStr: String?): LocalDate? {
        if (dateStr.isNullOrBlank()) return null
        val start = tryParseStartEnd(dateStr, true)
        return start?.let { LocalDate.from(it.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant()) }
    }

    fun parseEndDate(dateStr: String?): LocalDate? {
        if (dateStr.isNullOrBlank()) return null
        val end = tryParseStartEnd(dateStr, false)
        return end?.let { LocalDate.from(it.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant()) }
    }

    // 문자열 속 태그 제거 메서드
    private fun String.removeTag(): String{
        return this.replace(Regex("<[^>]*>"), "")
    }

    private fun tryParseStartEnd(dateStr: String, isStart: Boolean): LocalDate? {
        val cleaned = dateStr.replace("\\(.*?\\)".toRegex(), "")  // 요일 제거
        val patterns = listOf(
            "(\\d{4})\\.\\s?(\\d{1,2})\\.\\s?(\\d{1,2})\\s?~\\s?(\\d{1,2})",  // 2025. 5. 30. ~ 5. 31.
            "(\\d{4})\\.\\s?(\\d{1,2})\\.\\s?(\\d{1,2})\\s?~\\s?(\\d{4})\\.\\s?(\\d{1,2})\\.\\s?(\\d{1,2})", // 2024. 11. 15. ~ 2025. 02. 02
            "(\\d{4})년\\s?(\\d{1,2})월"  // 2024년 10월 예정
        )

        for (pattern in patterns) {
            val regex = Pattern.compile(pattern)
            val matcher = regex.matcher(cleaned)
            if (matcher.find()) {
                return when (pattern) {
                    patterns[0] -> { // 2025. 5. 30. ~ 5. 31.
                        val year = matcher.group(1).toInt()
                        val month = matcher.group(2).toInt()
                        val dayStart = matcher.group(3).toInt()
                        val dayEnd = matcher.group(4).toInt()
                        if (isStart) LocalDate.of(year, month, dayStart)
                        else LocalDate.of(year, month, dayEnd)
                    }
                    patterns[1] -> { // 2024. 11. 15. ~ 2025. 02. 02
                        if (isStart) LocalDate.of(matcher.group(1).toInt(), matcher.group(2).toInt(), matcher.group(3).toInt())
                        else LocalDate.of(matcher.group(4).toInt(), matcher.group(5).toInt(), matcher.group(6).toInt())
                    }
                    patterns[2] -> { // 2024년 10월 예정
                        val year = matcher.group(1).toInt()
                        val month = matcher.group(2).toInt()
                        if (isStart) LocalDate.of(year, month, 1)
                        else LocalDate.of(year, month, LocalDate.of(year, month, 1).lengthOfMonth())
                    }
                    else -> null
                }
            }
        }
        return null
    }


    fun getStatus(dateStr: String): FestivalStatus {

        if (dateStr.equals("")) {
            return FestivalStatus.UNKNOWN
        }

        val now = LocalDate.now()

        // 1. 정확한 기간 표시 (yyyy. MM. dd. ~ yyyy. MM. dd.)
        val fullDateRangeRegex = """(\d{4})\.\s*(\d{1,2})\.\s*(\d{1,2})\..*~\s*(\d{4})?\.\s*(\d{1,2})\.\s*(\d{1,2})""".toRegex()

        // 2. 월 단위, 예정 표시 (ex: 2024년 10월 예정, 2025.8월 중 (예정))
        val monthYearRegex = """(\d{4})[년.]?\s*(\d{1,2})[월.]?.*예정""".toRegex()

        fullDateRangeRegex.find(dateStr)?.let { match ->
            val startYear = match.groupValues[1].toInt()
            val startMonth = match.groupValues[2].toInt()
            val startDay = match.groupValues[3].toInt()
            val endYear = match.groupValues[4].ifEmpty { match.groupValues[1] }.toInt()
            val endMonth = match.groupValues[5].toInt()
            val endDay = match.groupValues[6].toInt()

            val startDate = LocalDate.of(startYear, startMonth, startDay)
            val endDate = LocalDate.of(endYear, endMonth, endDay)

            return when {
                now.isBefore(startDate) -> FestivalStatus.UPCOMING
                now.isAfter(endDate) -> FestivalStatus.COMPLETE
                else -> FestivalStatus.IN_PROGRESS
            }
        }

        monthYearRegex.find(dateStr)?.let { _ ->
            return FestivalStatus.UPCOMING
        }

        // 기타 알 수 없는 형식이면 UNKNOWN
        return FestivalStatus.UNKNOWN
    }


}