package busanVibe.busan.global.config.mongo

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration
import org.springframework.data.mongodb.core.convert.MongoCustomConversions
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

//@Configuration
//class MongoTimeConfig: AbstractMongoClientConfiguration() {
//
//    @Bean
//    override fun customConversions(): MongoCustomConversions {
//        return MongoCustomConversions(
//            listOf(
//                Converter<LocalDateTime, Date> { Date.from(it.atZone(ZoneId.of("Asia/Seoul")).toInstant()) },
//                Converter<Date, LocalDateTime> { it.toInstant().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime() }
//            )
//        )
//    }
//
//    override fun getDatabaseName(): String {
//        TODO("Not yet implemented")
//    }
//}