package bbangbbangz.baby_monitoring_system.controller;

import bbangbbangz.baby_monitoring_system.domain.User;
import bbangbbangz.baby_monitoring_system.repository.UserRepository;
import bbangbbangz.baby_monitoring_system.config.JWT.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public UserController(UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/me")
    @Operation(summary = "회원 정보 조회", description = "현재 로그인한 사용자의 정보를 반환합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<User> getUserInfo(HttpServletRequest request) {
        try {
            String token = extractToken(request); // Bearer 토큰 추출
            String userId = jwtTokenProvider.getUsername(token); // 토큰에서 사용자 ID 추출

            Optional<User> user = userRepository.findById(Long.valueOf(userId));
            return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(null); // 잘못된 요청
        } catch (Exception e) {
            return ResponseEntity.status(401).build(); // 인증 실패
        }
    }

    @PutMapping("/me")
    @Operation(summary = "회원 정보 수정", description = "현재 로그인한 사용자의 정보를 수정합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<User> updateUserInfo(HttpServletRequest request, @RequestBody User updatedUser) {
        try {
            String token = extractToken(request); // Bearer 토큰 추출
            String userId = jwtTokenProvider.getUsername(token); // 토큰에서 사용자 ID 추출

            Optional<User> optionalUser = userRepository.findById(Long.valueOf(userId));
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();

                if (updatedUser.getEmail() != null && !updatedUser.getEmail().equals(user.getEmail())) {
                    if (userRepository.findByEmail(updatedUser.getEmail()).isPresent()) {
                        return ResponseEntity.badRequest().body(null); // 이메일 중복 에러
                    }
                }

                user.setName(Optional.ofNullable(updatedUser.getName()).orElse(user.getName()));
                user.setEmail(Optional.ofNullable(updatedUser.getEmail()).orElse(user.getEmail()));

                userRepository.save(user);
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(null); // 잘못된 요청
        } catch (Exception e) {
            return ResponseEntity.status(401).build(); // 인증 실패
        }
    }

    private String extractToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization header is missing or invalid");
        }
        return authorizationHeader.substring(7); // Bearer 접두사 제거
    }
}
