package bbangbbangz.baby_monitoring_system.gateway;

import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AudioProcessorWebSocketHandler extends BinaryWebSocketHandler {

    private final Map<String, AudioBuffer> userAudioBuffers = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
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
                sendAudioToAIServer(audioData, sessionId);
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        userAudioBuffers.remove(session.getId());
        System.out.println("Connection closed: " + session.getId());
    }

    private void sendAudioToAIServer(byte[] audioData, String sessionId) {
        try {
            byte[] wavData = convertToWav(audioData);
            // AI 서버에 전송
            System.out.println("Sending audio for session " + sessionId + " to AI server: " + wavData.length + " bytes");

            // HttpClient httpClient = HttpClient.newHttpClient();
            // HttpRequest request = HttpRequest.newBuilder()
            //         .uri(URI.create("http://ai-server-url/process"))
            //         .header("Content-Type", "audio/wav")
            //         .POST(HttpRequest.BodyPublishers.ofByteArray(wavData))
            //         .build();
            // httpClient.send(request, HttpResponse.BodyHandlers.discarding());

        } catch (Exception e) {
            System.err.println("Error sending audio for session " + sessionId);
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