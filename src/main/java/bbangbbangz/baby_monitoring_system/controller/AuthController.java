package bbangbbangz.baby_monitoring_system.controller;

import bbangbbangz.baby_monitoring_system.dto.AuthRequest;
import bbangbbangz.baby_monitoring_system.dto.AuthResponse;
import bbangbbangz.baby_monitoring_system.dto.LoginRequest;
import bbangbbangz.baby_monitoring_system.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
            description = "유저 로그인 시 JWT 토큰을 반환합니다."
    )
    @ResponseBody
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest authRequest) {
        String token = authService.login(authRequest);
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/register")
    @Operation(
            summary = "유저 회원가입",
            description = "회원가입 후 성공 메시지를 반환합니다."
    )
    public ResponseEntity<String> register(@Valid @RequestBody AuthRequest authRequest) {
        authService.register(authRequest);
        return ResponseEntity.ok("User registered successfully");
    }
}
