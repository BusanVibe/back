package busanVibe.busan.domain.festival.util

import java.util.Date

class FestivalDateUtil {

    /**
     * 날짜까지만 표시하도록 하여 String 반환
     */
    fun removeTime(date: Date): String {
        return date.toString().substring(0, 10)
    }

}