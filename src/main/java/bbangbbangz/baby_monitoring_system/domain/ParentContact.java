package bbangbbangz.baby_monitoring_system.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "parent_contacts")
public class ParentContact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "parent_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ParentType parentType;

    @Column(name = "phone_number")
    private String phoneNumber;

    // Getters and Setters
    // omitted for brevity

    public enum ParentType {
        MOM, DAD
    }
}