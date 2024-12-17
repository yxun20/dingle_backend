package bbangbbangz.baby_monitoring_system.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;



@OpenAPIDefinition(
        info = @Info(
                title = "빵빵이조 API 명세서",
                description = "빵빵이조 API 명세서",
                version = "v1"
        ),
        servers = {
                @Server(url = "http://localhost:8080",
                        description = "LOCAL"),
                @Server(url = "https://bbang.justsloth.com",
                        description = "PRODUCTION")
        }

)
@Configuration
public class OpenApiConfig {

    private static final String BEARER_TOKEN_PREFIX = "Bearer";

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedOrigin("http://localhost:8080");
        config.addAllowedOrigin("https://bbang.justsloth.com");
        config.addAllowedMethod("*");

        //로컬 CORS 용
        source.registerCorsConfiguration("/**", config);
        source.registerCorsConfiguration("/v3/api-docs", config);
        return new CorsFilter(source);
    }

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