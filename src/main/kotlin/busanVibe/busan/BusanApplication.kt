package busanVibe.busan

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import java.util.TimeZone

@SpringBootApplication
@EnableJpaAuditing
class BusanApplication{
	@Bean
	fun init() = CommandLineRunner {
		// 서버 타임존 설정
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"))
	}
}

fun main(args: Array<String>) {
	runApplication<BusanApplication>(*args)
}
