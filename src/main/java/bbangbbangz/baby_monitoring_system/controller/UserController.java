package bbangbbangz.baby_monitoring_system.controller;

import bbangbbangz.baby_monitoring_system.config.JWT.JwtTokenProvider;
<<<<<<< HEAD
import bbangbbangz.baby_monitoring_system.domain.User;
import bbangbbangz.baby_monitoring_system.repository.UserRepository;
=======
import bbangbbangz.baby_monitoring_system.dto.MypageRequest;
import bbangbbangz.baby_monitoring_system.service.MypageService;
>>>>>>> yubin
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final JwtTokenProvider jwtTokenProvider;
    private final MypageService mypageService;

<<<<<<< HEAD
    public UserController(JwtTokenProvider jwtTokenProvider, UserRepository userRepository) {
=======
    public UserController(JwtTokenProvider jwtTokenProvider, MypageService mypageService) {
>>>>>>> yubin
        this.jwtTokenProvider = jwtTokenProvider;
        this.mypageService = mypageService;
    }

<<<<<<< HEAD
    @GetMapping("/me")
    @Operation(summary = "회원 정보 조회", description = "현재 로그인한 사용자의 정보를 반환합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<User> getUserInfo(HttpServletRequest request) {
        try {
            String token = extractToken(request);
            String userId = jwtTokenProvider.getUsername(token);

            Optional<User> user = userRepository.findById(Long.valueOf(userId));
            return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid token: " + e.getMessage());
            return ResponseEntity.status(400).body(null);
        } catch (Exception e) {
            System.err.println("Unauthorized access: " + e.getMessage());
            return ResponseEntity.status(401).build();
        }
    }

    @PutMapping("/me")
    @Operation(summary = "회원 정보 수정", description = "현재 로그인한 사용자의 정보를 수정합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<User> updateUserInfo(HttpServletRequest request, @RequestBody User updatedUser) {
        try {
            String token = extractToken(request);
            String userId = jwtTokenProvider.getUsername(token);

            Optional<User> optionalUser = userRepository.findById(Long.valueOf(userId));
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();

                if (updatedUser.getEmail() != null && !updatedUser.getEmail().equals(user.getEmail())) {
                    boolean emailExists = userRepository.existsByEmail(updatedUser.getEmail());
                    if (emailExists) {
                        return ResponseEntity.badRequest().body(null);
                    }
                }

                user.setName(Optional.ofNullable(updatedUser.getName()).orElse(user.getName()));
                user.setEmail(Optional.ofNullable(updatedUser.getEmail()).orElse(user.getEmail()));

                userRepository.save(user);
                return ResponseEntity.ok(user);
            }
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid token: " + e.getMessage());
            return ResponseEntity.status(400).body(null);
        } catch (Exception e) {
            System.err.println("Unauthorized access: " + e.getMessage());
            return ResponseEntity.status(401).build();
        }
=======
    @PostMapping("/me/details")
    @Operation(summary = "회원 상세 정보 등록", description = "회원가입 후 Baby와 ParentContact 정보를 등록합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> registerUserDetails(HttpServletRequest request, @Valid @RequestBody MypageRequest mypageRequest) {
        Long userId = extractUserId(request);
        mypageService.registerUserDetails(userId, mypageRequest);
        return ResponseEntity.ok(Map.of("message", "Details successfully registered."));
    }

    @GetMapping("/me")
    @Operation(summary = "회원 정보 조회", description = "현재 로그인한 사용자의 Baby와 ParentContact 정보를 반환합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> getUserDetails(HttpServletRequest request) {
        Long userId = extractUserId(request);
        var mypage = mypageService.getMypage(userId);
        return ResponseEntity.ok(mypage);
    }

    @PutMapping("/me")
    @Operation(summary = "회원 정보 수정", description = "현재 로그인한 사용자의 Baby와 ParentContact 정보를 수정합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> updateUserDetails(HttpServletRequest request, @Valid @RequestBody MypageRequest updatedMypageRequest) {
        Long userId = extractUserId(request);
        mypageService.updateUserDetails(userId, updatedMypageRequest);
        return ResponseEntity.ok(Map.of("message", "Details successfully updated."));
    }

    private Long extractUserId(HttpServletRequest request) {
        String token = extractToken(request);
        return Long.parseLong(jwtTokenProvider.getUsername(token));
>>>>>>> yubin
    }

    private String extractToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization header is missing or invalid");
        }
        return authorizationHeader.substring(7);
    }
}
