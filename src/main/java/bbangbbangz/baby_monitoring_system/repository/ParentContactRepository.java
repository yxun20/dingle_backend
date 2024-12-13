package bbangbbangz.baby_monitoring_system.repository;

import bbangbbangz.baby_monitoring_system.domain.ParentContact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParentContactRepository extends JpaRepository<ParentContact, Long> {
    Optional<ParentContact> findByUserId(Long userId);
}
