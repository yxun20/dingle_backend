package bbangbbangz.baby_monitoring_system.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
@Entity
@Table(name = "baby_statistics")
public class BabyStatistic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "baby_id", nullable = false)
    private Baby baby;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "fall_count", nullable = false)
    private int fallCount;

    @Column(name = "cry_count", nullable = false)
    private int cryCount;

    @Column(name = "choking_count", nullable = false)
    private int chokingCount;

    // Getters and Setters
}
