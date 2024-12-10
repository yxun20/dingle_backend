package bbangbbangz.baby_monitoring_system.gateway;

import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AudioProcessorWebSocketHandler extends BinaryWebSocketHandler {

    private final Map<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();
    private final Map<String, AudioBuffer> userAudioBuffers = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        userSessions.put(session.getId(), session);
        userAudioBuffers.put(session.getId(), new AudioBuffer());
        System.out.println("New connection established: " + session.getId());
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        String sessionId = session.getId();
        AudioBuffer buffer = userAudioBuffers.get(sessionId);

        if (buffer != null) {
            buffer.appendData(message.getPayload());

            // 10초 단위 데이터가 채워지면 WAV 파일로 변환하여 AI 서버로 전송
            if (buffer.isReadyForProcessing()) {
                byte[] audioData = buffer.getAndClearData();
                processAudioData(audioData, sessionId);
            }
        }
    }

    private void processAudioData(byte[] audioData, String sessionId) {
        try {
            byte[] wavData = convertToWav(audioData);

            // AI 서버로 전송
            byte[] aiResponse = sendToAIServer(wavData);

            // WebSocket 클라이언트로 전송
            sendToFrontend(aiResponse, sessionId);

        } catch (Exception e) {
            System.err.println("Error processing audio for session " + sessionId);
            e.printStackTrace();
        }
    }

    private byte[] convertToWav(byte[] rawAudioData) throws IOException {
        AudioFormat format = new AudioFormat(16000, 16, 1, true, false); // 16kHz, 16비트, 모노
        ByteArrayInputStream bais = new ByteArrayInputStream(rawAudioData);
        AudioInputStream audioInputStream = new AudioInputStream(bais, format, rawAudioData.length / format.getFrameSize());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, baos);

        return baos.toByteArray();
    }

    private byte[] sendToAIServer(byte[] wavData) throws IOException {
        URL url = new URL("ai-server-url");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "audio/wav");

        connection.getOutputStream().write(wavData);
        connection.getOutputStream().flush();

        ByteArrayOutputStream responseBuffer = new ByteArrayOutputStream();
        connection.getInputStream().transferTo(responseBuffer);

        return responseBuffer.toByteArray();
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

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        userSessions.remove(session.getId());
        userAudioBuffers.remove(session.getId());
        System.out.println("Connection closed: " + session.getId());
    }

    private static class AudioBuffer {
        private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        private static final int TEN_SECONDS_IN_BYTES = 16000 * 2 * 10; // 16kHz, 2 bytes per sample, 10 seconds

        public void appendData(ByteBuffer data) {
            try {
                buffer.write(data.array());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public boolean isReadyForProcessing() {
            return buffer.size() >= TEN_SECONDS_IN_BYTES;
        }

        public byte[] getAndClearData() {
            byte[] data = buffer.toByteArray();
            buffer.reset();
            return data;
        }
    }
}