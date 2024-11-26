package bbangbbangz.baby_monitoring_system.controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/baby")
public class BabyController {

    @GetMapping("/status")
    public String getBabyStatus() {
        // Replace with actual logic to fetch data from DB
        return "Baby is sleeping";
    }

    @PostMapping("/status")
    public String saveBabyStatus(@RequestBody String status) {
        // Replace with logic to save status to DB
        return "Status saved: " + status;
    }
}
