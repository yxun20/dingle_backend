package bbangbbangz.baby_monitoring_system.controller;

import bbangbbangz.baby_monitoring_system.dto.BabyStatusDTO;
import bbangbbangz.baby_monitoring_system.service.BabyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/baby")
public class BabyController {

    private final BabyService babyService;

    public BabyController(BabyService babyService) {
        this.babyService = babyService;
    }

    @Operation(
            summary = "아기 상태 조회",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/status")
    public ResponseEntity<BabyStatusDTO> getBabyStatus() {
        BabyStatusDTO babyStatus = babyService.getBabyStatus();
        return ResponseEntity.ok(babyStatus);
    }

    @Operation(
            summary = "아기 상태 저장",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/status")
    public ResponseEntity<String> saveBabyStatus(@RequestBody BabyStatusDTO statusDTO) {
        babyService.saveBabyStatus(statusDTO);
        return ResponseEntity.ok("Status saved: " + statusDTO.getStatus());
    }
}
