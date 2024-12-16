package bbangbbangz.baby_monitoring_system.dto;

import bbangbbangz.baby_monitoring_system.domain.Baby;
import bbangbbangz.baby_monitoring_system.domain.ParentContact;

import java.util.List;

public class MypageDto{

    private Baby baby;
    private List<ParentContact> parentContacts;

    // 생성자
    public MypageDto(Baby baby, List<ParentContact> parentContacts) {
        this.baby = baby;
        this.parentContacts = parentContacts;
    }

    // Getter, Setter
    public Baby getBaby() {
        return baby;
    }

    public void setBaby(Baby baby) {
        this.baby = baby;
    }

    public List<ParentContact> getParentContacts() {
        return parentContacts;
    }

    public void setParentContacts(List<ParentContact> parentContacts) {
        this.parentContacts = parentContacts;
    }
}
