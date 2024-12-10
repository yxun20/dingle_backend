package bbangbbangz.baby_monitoring_system.gateway;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VideoFrameExtractWebSocketHandler extends BinaryWebSocketHandler {

    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        ByteBuffer payload = message.getPayload();

        executorService.submit(() -> {
            try {
                processVideoData(payload.array(), session.getId());
            } catch (Exception e) {
                System.err.println("Error processing video for session " + session.getId());
                e.printStackTrace();
            }
        });
    }

    private void processVideoData(byte[] videoData, String sessionId) throws Exception {
        try (FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(new ByteArrayInputStream(videoData))) {
            frameGrabber.start();

            // 0번째 프레임 추출
            BufferedImage frameImage = extractFrame(frameGrabber, 0);

            if (frameImage != null) {
                byte[] imageBytes = convertImageToBytes(frameImage);
                sendToAIServer(imageBytes, sessionId);
            }

            frameGrabber.stop();
        }
    }

    private BufferedImage extractFrame(FFmpegFrameGrabber frameGrabber, int frameNumber) throws IOException {
        for (int i = 0; i <= frameNumber; i++) {
            if (frameGrabber.grab() == null) {
                return null; // 프레임이 없으면 null 반환
            }
        }

        Java2DFrameConverter converter = new Java2DFrameConverter();
        return converter.convert(frameGrabber.grab());
    }

    private byte[] convertImageToBytes(BufferedImage image) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            return baos.toByteArray();
        }
    }

    private void sendToAIServer(byte[] imageBytes, String sessionId) {
        // AI 서버로 이미지 전송 로직
        System.out.println("Sending frame for session " + sessionId + " to AI server");

        // HttpClient httpClient = HttpClient.newHttpClient();
        // HttpRequest request = HttpRequest.newBuilder()
        //         .uri(URI.create("http://ai-server-url/process"))
        //         .header("Content-Type", "image/png")
        //         .POST(HttpRequest.BodyPublishers.ofByteArray(imageBytes))
        //         .build();
        // httpClient.send(request, HttpResponse.BodyHandlers.discarding());
    }
}

