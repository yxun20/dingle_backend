package bbangbbangz.baby_monitoring_system.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "baby_id", nullable = false)
    private Baby baby;

    @ManyToOne
    @JoinColumn(name = "state_id")
    private BabyState state;

    @ManyToOne
    @JoinColumn(name = "contact_id", nullable = false) // 부모 연락처는 필수로 설정
    private ParentContact contact;

    @Column(name = "notification_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    @Column(name = "sent_at", nullable = false, updatable = false)
    private LocalDateTime sentAt;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationStatus status = NotificationStatus.PENDING; // 기본값 설정

    @PrePersist
    protected void onCreate() {
        this.sentAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Baby getBaby() {
        return baby;
    }

    public void setBaby(Baby baby) {
        if (baby == null) {
            throw new IllegalArgumentException("Baby cannot be null");
        }
        this.baby = baby;
    }

    public BabyState getState() {
        return state;
    }

    public void setState(BabyState state) {
        this.state = state;
    }

    public ParentContact getContact() {
        return contact;
    }

    public void setContact(ParentContact contact) {
        if (contact == null) {
            throw new IllegalArgumentException("Contact cannot be null");
        }
        this.contact = contact;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        if (notificationType == null) {
            throw new IllegalArgumentException("Notification type cannot be null");
        }
        this.notificationType = notificationType;
    }

    public LocalDateTime getSentAt() {
        return sentAt; // Setter 제거로 수정 방지
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public void setStatus(NotificationStatus status) {
        this.status = status;
    }

    // Enums
    public enum NotificationType {
        SMS, CALL, PUSH // 확장 가능성을 고려하여 주석 추가
    }

    public enum NotificationStatus {
        PENDING, SENT, FAILED // 확장 가능성을 고려하여 주석 추가
    }
}
