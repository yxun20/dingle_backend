package bbangbbangz.baby_monitoring_system.service;

import bbangbbangz.baby_monitoring_system.domain.Activity;
import bbangbbangz.baby_monitoring_system.dto.ActivityResponseDto;
import bbangbbangz.baby_monitoring_system.repository.ActivityRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;

    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    // 특정 날짜 범위에 해당하는 활동 내역 조회 (특정 사용자)
    public List<ActivityResponseDto> getActivitiesByDateAndUser(LocalDateTime startDate, LocalDateTime endDate, Long userId) {
        List<Activity> activities = activityRepository.findByTimestampBetweenAndUserId(startDate, endDate, userId);
        return activities.stream()
                .map(activity -> new ActivityResponseDto(
                        activity.getId(),
                        activity.getTimestamp(),
                        activity.getType(),
                        activity.getComment(),
                        activity.getUser().getName()
                ))
                .collect(Collectors.toList());
    }

    // 특정 사용자의 활동 내역 조회
    public List<ActivityResponseDto> getActivitiesByUser(Long userId) {
        List<Activity> activities = activityRepository.findByUserId(userId);
        return activities.stream()
                .map(activity -> new ActivityResponseDto(
                        activity.getId(),
                        activity.getTimestamp(),
                        activity.getType(),
                        activity.getComment(),
                        activity.getUser().getName()
                ))
                .collect(Collectors.toList());
    }
}
