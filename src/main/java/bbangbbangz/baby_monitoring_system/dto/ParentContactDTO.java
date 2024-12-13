package bbangbbangz.baby_monitoring_system.dto;

/**
 * 엄마와 아빠의 전화번호 정보를 전달하기 위한 DTO
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
