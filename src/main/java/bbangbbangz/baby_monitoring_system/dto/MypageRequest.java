package bbangbbangz.baby_monitoring_system.dto;


public class MypageRequest {
    private BabyRequest baby;
    private ParentContactRequest parentContacts;

    // Getters and Setters
    public BabyRequest getBaby() {
        return baby;
    }

    public void setBaby(BabyRequest baby) {
        this.baby = baby;
    }

    public ParentContactRequest getParentContacts() {
        return parentContacts;
    }

    public void setParentContacts(ParentContactRequest parentContacts) {
        this.parentContacts = parentContacts;
    }
}
