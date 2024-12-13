package bbangbbangz.baby_monitoring_system.controller;

import bbangbbangz.baby_monitoring_system.dto.ParentContactDTO;
import bbangbbangz.baby_monitoring_system.service.ParentContactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final ParentContactService parentContactService;

    public UserController(ParentContactService parentContactService) {
        this.parentContactService = parentContactService;
    }

    @GetMapping("/parent-contacts/{userId}")
    @Operation(summary = "부모 연락처 조회", description = "엄마와 아빠의 전화번호를 반환합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ParentContactDTO> getParentContacts(@PathVariable Long userId) {
        ParentContactDTO parentContactDTO = parentContactService.getParentContactsByUserId(userId);
        return ResponseEntity.ok(parentContactDTO);
    }

    @PostMapping("/parent-contacts")
    @Operation(summary = "부모 연락처 저장", description = "엄마와 아빠의 전화번호를 저장합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<String> saveParentContacts(@RequestBody ParentContactDTO parentContactDTO) {
        parentContactService.saveParentContacts(parentContactDTO);
        return ResponseEntity.ok("Parent contacts saved successfully");
    }
}
