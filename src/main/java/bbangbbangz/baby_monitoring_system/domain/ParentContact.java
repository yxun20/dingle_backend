package bbangbbangz.baby_monitoring_system.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "parent_contacts")
public class ParentContact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mom_phone_number", nullable = false)
    private String momPhoneNumber;

    @Column(name = "dad_phone_number", nullable = false)
    private String dadPhoneNumber;

    // Getters and Setters with Validation

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMomPhoneNumber() {
        return momPhoneNumber;
    }

    public void setMomPhoneNumber(String momPhoneNumber) {
        if (!isValidPhoneNumber(momPhoneNumber)) {
            throw new IllegalArgumentException("Invalid mom phone number format");
        }
        this.momPhoneNumber = momPhoneNumber;
    }

    public String getDadPhoneNumber() {
        return dadPhoneNumber;
    }

    public void setDadPhoneNumber(String dadPhoneNumber) {
        if (!isValidPhoneNumber(dadPhoneNumber)) {
            throw new IllegalArgumentException("Invalid dad phone number format");
        }
        this.dadPhoneNumber = dadPhoneNumber;
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        String phonePattern = "^[+]?[0-9]{10,15}$"; // 국제전화 번호 형식 검증
        return phoneNumber != null && phoneNumber.matches(phonePattern);
    }
}
