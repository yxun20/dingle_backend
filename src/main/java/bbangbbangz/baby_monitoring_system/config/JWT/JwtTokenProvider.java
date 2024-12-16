package bbangbbangz.baby_monitoring_system.config.JWT;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import java.util.Date;
//JwtTokenProvider는 JwtAuthenticationFilter와 AuthService에서 사용.

import javax.crypto.SecretKey;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtTokenProvider {

    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long EXPIRATION_TIME = 3600000;

    // JWT 생성
    public String createToken(String sub) {
        return Jwts.builder()
                .setSubject(sub)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    // JWT 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token);
            log.info("Token validated successfully: {}", token); // 검증 성공 로그 추가
            return true;
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token: {}", e.getMessage());  // 만료된 토큰 로그
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token: {}", e.getMessage()); // 지원되지 않는 토큰 로그
        } catch (MalformedJwtException e) {
            log.error("Malformed JWT token: {}", e.getMessage()); // 잘못된 형식의 토큰 로그
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage()); // 잘못된 서명 로그
        } catch (Exception e) {
            log.error("JWT validation failed: {}", e.getMessage()); // 기타 검증 실패 로그
        }
        return false; // 검증 실패 시 false 반환
    }

    // 사용자 이름 추출
    public String getUsername(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            log.info("Extracted username from token: {}", claims.getSubject());
            return claims.getSubject(); // 토큰에서 Subject 추출
        } catch (Exception e) {
            log.error("Failed to extract username: {}", e.getMessage()); // 추출 실패 로그
            throw new IllegalArgumentException("Invalid token"); // Invalid token 예외 던지기
        }
    }

}