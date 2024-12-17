package bbangbbangz.baby_monitoring_system.repository;

import bbangbbangz.baby_monitoring_system.domain.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    // 특정 날짜 범위와 사용자 ID를 기준으로 활동 조회
    List<Activity> findByTimestampBetweenAndUserId(LocalDateTime startDate, LocalDateTime endDate, Long userId);

    // 사용자 ID를 기준으로 활동 조회
    List<Activity> findByUserId(Long userId);
}
