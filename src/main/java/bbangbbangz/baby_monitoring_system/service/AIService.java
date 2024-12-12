package bbangbbangz.baby_monitoring_system.service;

import bbangbbangz.baby_monitoring_system.dto.AIResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

//Spring 서버에서 받은 AI 분석 결과 처리
@Service
public class AIService {
    // AI 응답 메시지 및 이미지 URL을 처리하는 로직 추가
    public void processAIResponse(AIResponse aiResponse) {
        // AI 응답 메시지 및 이미지 URL을 처리하는 로직 추가
        System.out.println("Received AI response: ");
        System.out.println("Risk Message: " + aiResponse.getRiskMessage());
        System.out.println("Image URL: " + aiResponse.getImageUrl());

        // 이미지 URL을 기반으로 이미지를 다운로드하거나 다른 로직 추가 가능
        downloadImage(aiResponse.getImageUrl());
    }

    private void downloadImage(String imageUrl) {
        // 이미지를 다운로드하는 로직 (예: HttpURLConnection을 사용하여 이미지 다운로드)
        try {
            RestTemplate restTemplate = new RestTemplate();
            byte[] imageBytes = restTemplate.getForObject(imageUrl, byte[].class);

            // 이미지 저장 로직 (예: static 디렉토리에 저장)
            // 예: 파일 이름은 UUID나 고유 ID로 생성 가능
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to download image from URL: " + imageUrl);
        }
    }
}
