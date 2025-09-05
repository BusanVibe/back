package busanVibe.busan.domain.tourApi.util

import busanVibe.busan.domain.festival.domain.Festival
import busanVibe.busan.domain.festival.domain.FestivalImage
import busanVibe.busan.domain.tourApi.dto.FestivalItem
import java.time.LocalDate
import java.util.regex.Pattern

class TourFestivalConverter {

    fun FestivalItem.toEntity(): Festival {
        println("${MAIN_TITLE} : ${USAGE_DAY_WEEK_AND_TIME}")
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
            startDate = startDate,
            endDate = endDate,
            place = MAIN_PLACE ?: "장소없음",
            introduction = ITEMCNTNTS?.removeTag() ?: "",
            fee = USAGE_AMOUNT?: "정보없음",
            phone = CNTCT_TEL ?: "정보없음",
            siteUrl = HOMEPAGE_URL ?: "정보없음",
            festivalLikes = emptySet(),
            contentId = UC_SEQ
        )

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
        return tryParseStartEnd(dateStr, true)
    }


    fun parseEndDate(dateStr: String?): LocalDate? {
        if (dateStr.isNullOrBlank()) return null
        return tryParseStartEnd(dateStr, false)
    }

    // 문자열 속 태그 제거 메서드
    private fun String.removeTag(): String{
        return this.replace(Regex("<[^>]*>"), "")
    }

    private fun toSafeInt(value: String?, default: Int): Int {
        return value?.toIntOrNull()?.takeIf { it > 0 } ?: default
    }

    private fun toSafeMonth(value: String?, default: Int = 1): Int {
        return value?.toIntOrNull()?.takeIf { it in 1..12 } ?: default
    }

    private fun toSafeDay(year: Int, month: Int, value: String?, default: Int = 1): Int {
        val maxDay = LocalDate.of(year, month, 1).lengthOfMonth()
        return value?.toIntOrNull()?.takeIf { it in 1..maxDay } ?: default
    }

    private fun tryParseStartEnd(dateStr: String, isStart: Boolean): LocalDate? {
        val cleaned = dateStr
            .replace("\\(.*?\\)".toRegex(), "") // 괄호 속 요일 제거
            .replace("예정", "")
            .replace("중", "")
            .trim()

        val patterns = listOf(
            // yyyy.MM.dd ~ yyyy.MM.dd
            """(\d{4})[.\-년\s]*(\d{1,2})[.\-월\s]*(\d{1,2})[.\s~]*(\d{4})[.\-년\s]*(\d{1,2})[.\-월\s]*(\d{1,2})""",
            // yyyy.MM.dd ~ MM.dd
            """(\d{4})[.\-년\s]*(\d{1,2})[.\-월\s]*(\d{1,2})[.\s~]*(\d{1,2})[.\-월\s]*(\d{1,2})""",
            // yyyy.MM.dd 단일 날짜
            """(\d{4})[.\-년\s]*(\d{1,2})[.\-월\s]*(\d{1,2})""",
            // yyyy.MM (월만 있는 경우)
            """(\d{4})[.\-년\s]*(\d{1,2})[.\-월\s]*"""
        )

        for (pattern in patterns) {
            val matcher = Pattern.compile(pattern).matcher(cleaned)
            if (matcher.find()) {
                return when (pattern) {
                    patterns[0] -> {
                        val startYear = matcher.group(1).toInt()
                        val startMonth = toSafeMonth(matcher.group(2))
                        val startDay = toSafeDay(startYear, startMonth, matcher.group(3))
                        val endYear = matcher.group(4).toInt()
                        val endMonth = toSafeMonth(matcher.group(5))
                        val endDay = toSafeDay(endYear, endMonth, matcher.group(6), LocalDate.of(endYear, endMonth, 1).lengthOfMonth())

                        if (isStart) LocalDate.of(startYear, startMonth, startDay)
                        else LocalDate.of(endYear, endMonth, endDay)
                    }
                    patterns[1] -> {
                        val year = matcher.group(1).toInt()
                        val monthStart = matcher.group(2).toInt()
                        val dayStart = toSafeInt(matcher.group(3), 1)
                        val monthEnd = matcher.group(4).toInt()
                        val dayEnd = toSafeInt(matcher.group(5), LocalDate.of(year, monthEnd, 1).lengthOfMonth())

                        if (isStart) LocalDate.of(year, monthStart, dayStart)
                        else LocalDate.of(year, monthEnd, dayEnd)
                    }
                    patterns[2] -> {
                        val year = matcher.group(1).toInt()
                        val month = matcher.group(2).toInt()
                        val day = toSafeInt(matcher.group(3), 1)
                        LocalDate.of(year, month, day)
                    }
                    patterns[3] -> {
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


}