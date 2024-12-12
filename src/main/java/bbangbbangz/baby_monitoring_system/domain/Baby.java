package bbangbbangz.baby_monitoring_system.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private LocalDate birthDate; // LocalDate로 변경

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "baby", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BabyState> babyStates = new ArrayList<>(); // 초기화

    @OneToMany(mappedBy = "baby", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BabyStatistic> babyStatistics = new ArrayList<>(); // 초기화

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getBabyName() {
        return babyName;
    }

    public void setBabyName(String babyName) {
        this.babyName = babyName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<BabyState> getBabyStates() {
        return babyStates;
    }

    public void setBabyStates(List<BabyState> babyStates) {
        this.babyStates = babyStates;
    }

    public List<BabyStatistic> getBabyStatistics() {
        return babyStatistics;
    }

    public void setBabyStatistics(List<BabyStatistic> babyStatistics) {
        this.babyStatistics = babyStatistics;
    }

    public enum Gender {
        MALE, FEMALE
    }
}
