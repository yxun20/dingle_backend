package bbangbbangz.baby_monitoring_system.config;

import bbangbbangz.baby_monitoring_system.config.JWT.JwtAuthenticationFilter;
import bbangbbangz.baby_monitoring_system.config.JWT.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers(
                        "/auth/**",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/swagger-ui.html",
                        "/video",
                        "/audio"
                ).permitAll() // 회원가입 및 로그인 요청은 인증 없이 허용
                .anyRequest().authenticated() // 나머지 요청은 인증 필요
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService),
                        UsernamePasswordAuthenticationFilter.class); // 필터 추가

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
