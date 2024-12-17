package bbangbbangz.baby_monitoring_system.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 하나의 LocalDateTime으로 합침
    private LocalDateTime timestamp;
    
    private String type;
    private String comment;

    // User와의 관계 추가
    @ManyToOne
    @JoinColumn(name = "user_id") // 외래키 컬럼 지정
    private User user;

    // 기본 생성자
    public Activity() {}

    // 모든 필드가 포함된 생성자
    public Activity(LocalDateTime timestamp, String type, String comment, User user) {
        this.timestamp = timestamp;
        this.type = type;
        this.comment = comment;
        this.user = user;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
