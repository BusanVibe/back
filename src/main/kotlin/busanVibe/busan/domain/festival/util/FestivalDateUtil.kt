package busanVibe.busan.domain.festival.util

import java.time.LocalDate

class FestivalDateUtil {

    /**
     * 날짜까지만 표시하도록 하여 String 반환
     */
    fun removeTime(date: LocalDate?): String {
        date?: return ""
        val formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy.MM.dd")
        return formatter.format(date)
    }

}