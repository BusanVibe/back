package busanVibe.busan.global.config.mongo

import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

@Component
@ReadingConverter
class DateToLocalDateTimeKstConverter: Converter<Date, LocalDateTime> {

    override fun convert(source: Date): LocalDateTime =
        source.toInstant().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime()
}