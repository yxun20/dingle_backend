package bbangbbangz.baby_monitoring_system.config.JWT;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import java.util.Date;
//JwtTokenProvider는 JwtAuthenticationFilter와 AuthService에서 사용.

// JWT를 생성하고 유효성을 검증하여 사용자 인증에 활용
@Component
public class JwtTokenProvider {
    private final String secretKey = "YourSecretKey";
    private final long validityInMilliseconds = 3600000; // 1 hour

    //사용자 이름을 기반으로 JWT 토큰 생성
    public String createToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + validityInMilliseconds))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }
    
    // 클라이언트가 보낸 JWT 토큰이 유효한지 검증증
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // JWT 토큰에서 사용자 이름 (Subject 추출)
    public String getUsername(String token) {
        return Jwts.parser().setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
