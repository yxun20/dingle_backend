package bbangbbangz.baby_monitoring_system.dto;

public class ParentContactRequest {
    private String momPhoneNumber;
    private String dadPhoneNumber;

    // Getters and Setters
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
}
