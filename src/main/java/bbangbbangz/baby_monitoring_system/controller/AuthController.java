package bbangbbangz.baby_monitoring_system.controller;


import bbangbbangz.baby_monitoring_system.dto.AuthRequest;
import bbangbbangz.baby_monitoring_system.dto.AuthResponse;
import bbangbbangz.baby_monitoring_system.dto.LoginRequest;
import bbangbbangz.baby_monitoring_system.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//로그인 및 회원가입 
// JWT 생성 및 반환을 테스트하기 위해 REST API로 호출
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    
    @PostMapping("/login")
    @Operation(
            summary = "유저 로그인",
            description = "유저 로그인시 JWT 토큰을 반환합니다."
    )
    @ResponseBody
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest authRequest) {
        String token = authService.login(authRequest);
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/register")
    @Operation(
            summary = "유저 회원가입"
    )
    public ResponseEntity<String> register(@RequestBody AuthRequest authRequest) {
        authService.register(authRequest);
        return ResponseEntity.ok("User registered successfully");
    }
}