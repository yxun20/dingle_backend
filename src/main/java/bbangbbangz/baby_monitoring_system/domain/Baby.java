package bbangbbangz.baby_monitoring_system.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "babies")
public class Baby {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "baby_name", nullable = false)
    private String babyName;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate; // LocalDate로 변경

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public enum Gender {
        MALE, FEMALE
    }
}
