package bbangbbangz.baby_monitoring_system.config;

import bbangbbangz.baby_monitoring_system.gateway.AudioProcessorWebSocketHandler;
import bbangbbangz.baby_monitoring_system.gateway.VideoFrameExtractWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new VideoFrameExtractWebSocketHandler(), "/stream")
                .setAllowedOrigins("*");
        registry.addHandler(new AudioProcessorWebSocketHandler(), "/audio")
                .setAllowedOrigins("*");
    }
}