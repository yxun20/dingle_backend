package bbangbbangz.baby_monitoring_system.controller;

import bbangbbangz.baby_monitoring_system.config.JWT.JwtTokenProvider;
import bbangbbangz.baby_monitoring_system.domain.User;
import bbangbbangz.baby_monitoring_system.dto.MypageDto;
import bbangbbangz.baby_monitoring_system.dto.ParentContactDTO;
import bbangbbangz.baby_monitoring_system.repository.UserRepository;
import bbangbbangz.baby_monitoring_system.service.ParentContactService;
import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final ParentContactService parentContactService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public UserController(ParentContactService parentContactService, JwtTokenProvider jwtTokenProvider, UserRepository userRepository) {
        this.parentContactService = parentContactService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    @Operation(summary = "회원 정보 조회-유빈", description = "현재 로그인한 사용자의 정보를 반환합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<MypageDto> getUserInfo(HttpServletRequest request) {
        try {
            String token = extractToken(request); // Bearer 토큰 추출
            if (!jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.status(403).build(); // 토큰이 유효하지 않으면 403 반환
            }

            String userId = jwtTokenProvider.getUsername(token); // 토큰에서 사용자 ID 추출
            Optional<User> user = userRepository.findById(Long.valueOf(userId));

            // MypageDto로 변환하여 필요한 데이터만 반환
            return user.map(u -> ResponseEntity.ok(new MypageDto(u.getBaby(), u.getParentContacts())))
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(null); // 잘못된 요청
        } catch (ExpiredJwtException e) {
            log.error("Token expired", e);
            return ResponseEntity.status(403).build(); // 만료된 토큰 처리
        } catch (Exception e) {
            log.error("Authentication failed", e);
            return ResponseEntity.status(403).build(); // 인증 실패
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
