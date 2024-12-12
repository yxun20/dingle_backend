package bbangbbangz.baby_monitoring_system.repository;

import bbangbbangz.baby_monitoring_system.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
