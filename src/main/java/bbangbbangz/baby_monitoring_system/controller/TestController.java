package bbangbbangz.baby_monitoring_system.controller;
import bbangbbangz.baby_monitoring_system.domain.TestEntity;
import bbangbbangz.baby_monitoring_system.repository.TestRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TestController {

    private final TestRepository testRepository;

    public TestController(TestRepository testRepository) {
        this.testRepository = testRepository;
    }

    @PostMapping("/add")
    public String addEntity(@RequestParam String name) {
        TestEntity entity = new TestEntity(name);
        testRepository.save(entity);
        return "Entity saved with name: " + name;
    }

    @GetMapping("/list")
    public List<TestEntity> listEntities() {
        return testRepository.findAll();
    }
}
