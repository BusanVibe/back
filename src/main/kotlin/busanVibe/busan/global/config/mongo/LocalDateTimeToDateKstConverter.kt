package busanVibe.busan.global.config.mongo

import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.WritingConverter
import org.springframework.stereotype.Component
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

@Component
@WritingConverter
class LocalDateTimeToDateKstConverter: Converter<LocalDateTime, Date> {

    override fun convert(source: LocalDateTime): Date =
        Date.from(source.atZone(ZoneId.of("Asia/Seoul")).toInstant())

}