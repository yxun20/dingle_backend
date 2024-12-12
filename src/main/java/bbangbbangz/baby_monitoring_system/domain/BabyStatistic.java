package bbangbbangz.baby_monitoring_system.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

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

    @PrePersist
    protected void validateDates() {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
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

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public int getFallCount() {
        return fallCount;
    }

    public void setFallCount(int fallCount) {
        if (fallCount < 0) {
            throw new IllegalArgumentException("Fall count cannot be negative");
        }
        this.fallCount = fallCount;
    }

    public int getCryCount() {
        return cryCount;
    }

    public void setCryCount(int cryCount) {
        if (cryCount < 0) {
            throw new IllegalArgumentException("Cry count cannot be negative");
        }
        this.cryCount = cryCount;
    }

    public int getChokingCount() {
        return chokingCount;
    }

    public void setChokingCount(int chokingCount) {
        if (chokingCount < 0) {
            throw new IllegalArgumentException("Choking count cannot be negative");
        }
        this.chokingCount = chokingCount;
    }
}
