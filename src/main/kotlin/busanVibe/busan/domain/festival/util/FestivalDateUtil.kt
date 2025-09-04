package busanVibe.busan.domain.festival.util

import java.text.SimpleDateFormat
import java.util.Date

class FestivalDateUtil {

    /**
     * 날짜까지만 표시하도록 하여 String 반환
     */
    fun removeTime(date: Date): String {
        val formatter: SimpleDateFormat = SimpleDateFormat("yyyy.MM.dd")
        return formatter.format(date)
    }

}