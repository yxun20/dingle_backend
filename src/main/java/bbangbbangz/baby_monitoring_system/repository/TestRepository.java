package bbangbbangz.baby_monitoring_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import bbangbbangz.baby_monitoring_system.domain.TestEntity;

@Repository
public interface TestRepository extends JpaRepository<TestEntity, Long> {
}
