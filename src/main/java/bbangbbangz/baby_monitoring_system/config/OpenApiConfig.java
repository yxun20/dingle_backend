package bbangbbangz.baby_monitoring_system.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "빵빵이조 API 명세서",
                description = "빵빵이조 API 명세서",
                version = "v1"
        )
)
@Configuration
public class OpenApiConfig {

    private static final String BEARER_TOKEN_PREFIX = "Bearer";

    @Bean
    public OpenAPI customOpenAPI() {
        String securitySchemeName = "bearerAuth";

        Components components = new Components()
                .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                        .name(securitySchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme(BEARER_TOKEN_PREFIX)
                        .bearerFormat("JWT"));

        return new OpenAPI()
                .components(components);
    }
}