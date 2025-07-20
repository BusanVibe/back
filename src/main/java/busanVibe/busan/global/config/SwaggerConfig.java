package busanVibe.busan.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI busanVibeApi() {

    Info info = new Info()
        .title("BusanVibe API")
        .description("BusanVibe API 명세서")
        .version("1.0.0");

    String jwtSchemeName = "JWT TOKEN";

    // API 요청 헤더에 인증 정보 포함
    SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);

    Components components =
        new Components()
            .addSecuritySchemes(
                jwtSchemeName,
                new SecurityScheme()
                    .name(jwtSchemeName)
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT"));
    OpenAPI openAPI =
        new OpenAPI()
            .info(info)
            .addServersItem(
                new Server()
                    .url("http://localhost:8080")
                    .description("Local server")) // 서버 URL 설정
            .addServersItem(
                new Server()
                    .url("https://api.busanVibe.site")
                    .description("Production server"))
            .addSecurityItem(securityRequirement)
            .components(components);

    openAPI.addExtension("x-swagger-ui-disable-cache", true);
    return openAPI;

  }

}
