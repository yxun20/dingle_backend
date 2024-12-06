package bbangbbangz.baby_monitoring_system.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
@Entity
@Table(name = "babies")
public class Baby {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "baby_name", nullable = false)
    private String babyName;

    @Column(name = "birth_date", nullable = false)
    private LocalDateTime birthDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "baby", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BabyState> babyStates;

    @OneToMany(mappedBy = "baby", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BabyStatistic> babyStatistics;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    // omitted for brevity

    public enum Gender {
        MALE, FEMALE
    }
}
