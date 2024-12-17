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

    @OneToOne
    @JoinColumn(name = "user_id") // 외래 키 설정
    private User user;

    // Getters and Setters
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
        this.momPhoneNumber = momPhoneNumber;
    }

    public String getDadPhoneNumber() {
        return dadPhoneNumber;
    }

    public void setDadPhoneNumber(String dadPhoneNumber) {
        this.dadPhoneNumber = dadPhoneNumber;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
