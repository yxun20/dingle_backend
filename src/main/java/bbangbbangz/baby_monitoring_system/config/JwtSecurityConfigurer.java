package bbangbbangz.baby_monitoring_system.config;

import bbangbbangz.baby_monitoring_system.config.JWT.JwtAuthenticationFilter;
import bbangbbangz.baby_monitoring_system.config.JWT.JwtTokenProvider;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//JwtAuthenticationFilter를 Security 필터 체인에 추가.
//JwtTokenProvider를 통해 JWT 검증 로직 수행.

public class JwtSecurityConfigurer extends SecurityConfigurerAdapter<HttpSecurity, HttpSecurity> {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtSecurityConfigurer(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService);
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }

}