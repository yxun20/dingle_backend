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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class VideoFrameExtractWebSocketHandler extends BinaryWebSocketHandler {

    private final Map<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();
    private final Map<String, ByteArrayOutputStream> userStreamBuffers = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String userId = Objects.requireNonNull(session.getUri()).getQuery().split("=")[1]; // 쿼리에서 userId 추출
        userSessions.put(userId, session); // 유저별로 WebSocket 세션을 저장
        System.out.println("WebSocket 연결 성공 - UserID: " + userId + ", SessionID: " + session.getId());
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws IOException {
        String sessionId = session.getId();
        System.out.println("Binary message 수신 - SessionID: " + sessionId + ", Message Size: " + message.getPayloadLength());

        // 세션에 맞는 버퍼를 가져오기
        ByteArrayOutputStream buffer = userStreamBuffers.computeIfAbsent(sessionId, k -> new ByteArrayOutputStream());

        // 받은 청크 데이터를 버퍼에 추가
        buffer.write(message.getPayload().array());

        // 청크가 모두 수신되었는지 확인 (예: 특정 길이가 되면 처리)
        if (isCompleteMessage(buffer)) {
            // 전체 메시지가 완성되었으면 처리 시작
            processCompleteMessage(buffer.toByteArray(), sessionId);

            // 버퍼 비우기
            buffer.reset();
        }
    }

    private boolean isCompleteMessage(ByteArrayOutputStream buffer) {
        return buffer.size() >= 100 * 1024;
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) {
        userSessions.remove(session.getId());
        System.out.println("Video connection closed: " + session.getId());
    }


    private void processCompleteMessage(byte[] completeMessage, String sessionId) {
        try {
            System.out.println("Received complete video stream of size: " + completeMessage.length);

            // 원본 비디오 스트림 데이터를 클라이언트로 전송
            sendOriginalStreamToFrontend(completeMessage, sessionId);

            // 바이너리 데이터를 FFmpegFrameGrabber로 처리
            BufferedImage frame = extractFrame(completeMessage);

            if (frame != null) {
                // 특정 프레임을 PNG로 변환
                byte[] extractedFrame = convertImageToPng(frame);

                // AI 서버로 프레임 전송
                byte[] aiResponse = sendToAIServer(extractedFrame);

                // AI 서버의 응답 데이터를 WebSocket을 통해 클라이언트로 전송
                sendToFrontend(aiResponse, sessionId);
            }
        } catch (Exception e) {
            System.err.println("Error processing video for session: " + sessionId);
            e.printStackTrace();
        }
    }

    private void sendOriginalStreamToFrontend(byte[] videoStream, String sessionId) {
        WebSocketSession session = userSessions.get(sessionId);

        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new BinaryMessage(videoStream));
                System.out.println("Original video stream sent to frontend for session: " + sessionId);
            } catch (IOException e) {
                System.err.println("Error sending original video stream to frontend for session: " + sessionId);
                e.printStackTrace();
            }
        }
    }

    private void sendToFrontend(byte[] aiResponse, String sessionId) {
        WebSocketSession session = userSessions.get(sessionId);

        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new BinaryMessage(aiResponse));
                System.out.println("AI response sent to frontend for session: " + sessionId);
            } catch (IOException e) {
                System.err.println("Error sending AI response to frontend for session: " + sessionId);
                e.printStackTrace();
            }
        }
    }

    private BufferedImage extractFrame(byte[] videoData) throws IOException {
        try (FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(new ByteArrayInputStream(videoData))) {
            Java2DFrameConverter frameConverter = new Java2DFrameConverter();

            // FFmpegFrameGrabber 초기화
            frameGrabber.start();

            // 첫 번째 프레임 추출
            BufferedImage frameImage = null;
            if (frameGrabber.grabImage() != null) {
                frameImage = frameConverter.convert(frameGrabber.grabImage());
            }

            frameGrabber.stop();
            return frameImage;
        } catch (Exception e) {
            System.err.println("Error extracting frame using FFmpeg: " + e.getMessage());
            throw new IOException("Frame extraction failed", e);
        }
    }

    private byte[] convertImageToPng(BufferedImage frame) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(frame, "png", baos);
        return baos.toByteArray();
    }

    private byte[] sendToAIServer(byte[] frameData) throws IOException {
        URL url = new URL("http://localhost:8000/predict/");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "image/png");

        connection.getOutputStream().write(frameData);
        connection.getOutputStream().flush();

        ByteArrayOutputStream responseBuffer = new ByteArrayOutputStream();
        connection.getInputStream().transferTo(responseBuffer);

        return responseBuffer.toByteArray();
    }
}