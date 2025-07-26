package busanVibe.busan.global.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun openAPI(): OpenAPI{

        val info: Info = Info()
            .title("BusanVibe API")
            .description("BusanVibe API 명세서")
            .version("1.0.0")

        val jwtSchemaName: String = "JWT TOKEN"

        // API 요청 헤더에 인증 정보 포함
        val securityRequirement: SecurityRequirement = SecurityRequirement().addList(jwtSchemaName)

        val components: Components =
            Components().addSecuritySchemes(
                jwtSchemaName,
                SecurityScheme()
                    .name(jwtSchemaName)
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT"))

        val openAPI: OpenAPI = OpenAPI()
            .info(info)
            .addServersItem(
                Server()
                    .url("http://localhost:8080")
                    .description("Local server")) // 서버 URL 설정
            .addServersItem(
                Server()
                    .url("https://api.busanVibe.site")
                    .description("Production server"))
            .addSecurityItem(securityRequirement)
            .components(components)

        openAPI.addExtension("x-swagger-ui-disable-cache", true)

        return openAPI
    }


}