package bbangbbangz.baby_monitoring_system.dto;

import java.util.List;

public class MypageRequest {
    private BabyRequest baby;
    private List<ParentContactRequest> parentContacts;

    // Getters and Setters
    public BabyRequest getBaby() {
        return baby;
    }

    public void setBaby(BabyRequest baby) {
        this.baby = baby;
    }

    public List<ParentContactRequest> getParentContacts() {
        return parentContacts;
    }

    public void setParentContacts(List<ParentContactRequest> parentContacts) {
        this.parentContacts = parentContacts;
    }
}
