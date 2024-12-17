package bbangbbangz.baby_monitoring_system.dto;

import java.time.LocalDate;

public class BabyRequest {
    private String babyName;
    private LocalDate birthDate;

    // Getters and Setters
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
}
