package bbangbbangz.baby_monitoring_system.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
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

    @Column(name = "detected_at", nullable = false)
    private LocalDateTime detectedAt;

    @PrePersist
    protected void onCreate() {
        this.detectedAt = LocalDateTime.now();
    }

    // Getters and Setters
    // omitted for brevity

    public enum StateType {
        FALL, CRY, CHOKING
    }
}