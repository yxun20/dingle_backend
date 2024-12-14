package bbangbbangz.baby_monitoring_system.dto;

//AIResponse
// AI 서버로부터 받은 메시지나 상태 코드를 저장
public class AIResponse {
    private String riskMessage;
    private String imageUrl;

    // Getter and Setter
    public String getRiskMessage() {
        return riskMessage;
    }

    public void setRiskMessage(String riskMessage) {
        this.riskMessage = riskMessage;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
