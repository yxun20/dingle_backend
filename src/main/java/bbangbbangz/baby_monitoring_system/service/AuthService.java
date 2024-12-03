package bbangbbangz.baby_monitoring_system.service;

import bbangbbangz.baby_monitoring_system.config.JWT.JwtTokenProvider;
import bbangbbangz.baby_monitoring_system.domain.User;
import bbangbbangz.baby_monitoring_system.repository.UserRepository;
import bbangbbangz.baby_monitoring_system.dto.AuthRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

//JwtSecurityConfigurer를 Security 설정에 추가하여 인증 흐름 통합.
//Security 설정의 중심 역할 수행.

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // 사용자 등록
    public void register(AuthRequest authRequest) {
        User user = new User();
        user.setUsername(authRequest.getUsername());
        user.setPassword(passwordEncoder.encode(authRequest.getPassword()));
        userRepository.save(user);
        System.out.println("User registered: " + authRequest.getUsername());
    
    }

    // 사용자 로그인 및 JWT 생성
    public String login(AuthRequest authRequest) {
         // 사용자 확인
        User user = userRepository.findByUsername(authRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 비밀번호 확인
        if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // JWT 생성
        String jwt = jwtTokenProvider.createToken(user.getUsername());
        System.out.println("Generated JWT for user: " + user.getUsername());
        System.out.println("JWT: " + jwt); // 생성된 JWT 출력

        return jwt;
    }
}