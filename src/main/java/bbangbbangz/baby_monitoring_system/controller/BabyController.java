package bbangbbangz.baby_monitoring_system.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/baby")
public class BabyController {

    @Operation(
            summary = "아기 상태 조회",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/status")
    public String getBabyStatus() {
        // Replace with actual logic to fetch data from DB
        return "Baby is sleeping";
    }


    @Operation(
            summary = "아기 상태 저장",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/status")
    public String saveBabyStatus(@RequestBody String status) {
        // Replace with logic to save status to DB
        return "Status saved: " + status;
    }
}
