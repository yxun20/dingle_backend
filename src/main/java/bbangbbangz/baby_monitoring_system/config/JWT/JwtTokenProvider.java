package bbangbbangz.baby_monitoring_system.config.JWT;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import java.util.Date;
//JwtTokenProvider는 JwtAuthenticationFilter와 AuthService에서 사용.

import javax.crypto.SecretKey;

// JWT를 생성하고 유효성을 검증하여 사용자 인증에 활용
@Component
public class JwtTokenProvider {
    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256); // 안전한 키 생성
    private static final long EXPIRATION_TIME = 3600000; // 1 hour in milliseconds

    // JWT 생성
    public String createToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }
    
    
    // 클라이언트가 보낸 JWT 토큰이 유효한지 검증증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY) // 검증에도 같은 키 사용
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
     // JWT에서 사용자 이름 추출
     public String getUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
