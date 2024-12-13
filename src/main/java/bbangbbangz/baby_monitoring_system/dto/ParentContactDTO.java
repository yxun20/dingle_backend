package bbangbbangz.baby_monitoring_system.dto;

/**
 * 부모 연락처를 나타내는 DTO
 */
public class ParentContactDTO {

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
