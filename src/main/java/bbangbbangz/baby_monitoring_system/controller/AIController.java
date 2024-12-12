package bbangbbangz.baby_monitoring_system.controller;

import bbangbbangz.baby_monitoring_system.dto.AIResponse;
import bbangbbangz.baby_monitoring_system.service.AIService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AIController {
    @Autowired
    private AIService aiService;

    @PostMapping("/ai_response")
    @Operation(
            summary = "AI 분석 결과 처리",
            description = "FastAPI 서버로부터 전달된 AI 분석 결과를 처리하고 필요한 작업을 수행합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "AI 분석 결과가 성공적으로 처리되었습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.")
    })
    public ResponseEntity<String> handleAIResponse(@RequestBody AIResponse aiResponse) {
        try {
            // AI 분석 결과 처리
            aiService.processAIResponse(aiResponse);
            return ResponseEntity.ok("AI response successfully processed.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid AI response: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error processing AI response: " + e.getMessage());
        }
    }
}
