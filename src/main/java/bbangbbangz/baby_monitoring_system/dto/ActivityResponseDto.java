package bbangbbangz.baby_monitoring_system.dto;

import java.time.LocalDateTime;

public class ActivityResponseDto {

    private Long id;
    private LocalDateTime timestamp;
    private String type;
    private String comment;
    private String userName;  // 사용자 이름 추가

    // 생성자
    public ActivityResponseDto(Long id, LocalDateTime timestamp, String type, String comment, String userName) {
        this.id = id;
        this.timestamp = timestamp;
        this.type = type;
        this.comment = comment;
        this.userName = userName;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getType() {
        return type;
    }

    public String getComment() {
        return comment;
    }

    public String getUserName() {
        return userName;
    }
}
