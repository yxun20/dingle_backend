package bbangbbangz.baby_monitoring_system.controller;

import bbangbbangz.baby_monitoring_system.config.JWT.JwtTokenProvider;
import bbangbbangz.baby_monitoring_system.dto.ActivityResponseDto;
import bbangbbangz.baby_monitoring_system.service.ActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/activities")
public class ActivityController {

    private final ActivityService activityService;
    private final JwtTokenProvider jwtTokenProvider;

    public ActivityController(ActivityService activityService, JwtTokenProvider jwtTokenProvider) {
        this.activityService = activityService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // 특정 날짜 범위에 해당하는 활동 내역 조회 (로그인한 사용자)
    @GetMapping("/date")
    @Operation(summary = "로그인한 사용자의 활동 내역 조회", description = "현재 로그인한 사용자의 활동 내역을 특정 날짜 범위로 조회합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<ActivityResponseDto>> getActivitiesByDate(
            HttpServletRequest request,
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        Long userId = extractUserId(request);
        List<ActivityResponseDto> activities = activityService.getActivitiesByDateAndUser(startDate, endDate, userId);
        return ResponseEntity.ok(activities);
    }

    // 현재 로그인한 사용자의 활동 내역 조회
    @GetMapping("/me")
    @Operation(summary = "로그인한 사용자의 활동 내역 조회", description = "현재 로그인한 사용자의 활동 내역을 조회합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<ActivityResponseDto>> getActivitiesByUser(HttpServletRequest request) {
        Long userId = extractUserId(request);
        List<ActivityResponseDto> activities = activityService.getActivitiesByUser(userId);
        return ResponseEntity.ok(activities);
    }

    // 사용자 ID 추출
    private Long extractUserId(HttpServletRequest request) {
        String token = extractToken(request);
        return Long.parseLong(jwtTokenProvider.getUsername(token));
    }

    // JWT 토큰 추출
    private String extractToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization header is missing or invalid");
        }
        return authorizationHeader.substring(7);
    }
}
