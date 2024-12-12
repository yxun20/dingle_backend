package bbangbbangz.baby_monitoring_system.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "baby_states")
public class BabyState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "baby_id", nullable = false)
    private Baby baby;

    @Column(name = "state_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private StateType stateType;

    @Column(name = "state_details")
    private String stateDetails;

    @Column(name = "detected_at", nullable = false, updatable = false)
    private LocalDateTime detectedAt;

    @PrePersist
    protected void onCreate() {
        this.detectedAt = LocalDateTime.now();
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
        this.baby = baby;
    }

    public StateType getStateType() {
        return stateType;
    }

    public void setStateType(StateType stateType) {
        this.stateType = stateType;
    }

    public String getStateDetails() {
        return stateDetails;
    }

    public void setStateDetails(String stateDetails) {
        this.stateDetails = stateDetails;
    }

    public LocalDateTime getDetectedAt() {
        return detectedAt;
    }

    // Enums
    public enum StateType {
        FALL, CRY, CHOKING
    }
}
