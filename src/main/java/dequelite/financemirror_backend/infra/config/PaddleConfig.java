package dequelite.financemirror_backend.infra.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaddleConfig {

    @Value("${paddle.mode}")
    private String mode;

    @Bean
    public String paddleBaseUrl() {
        return "live".equalsIgnoreCase(mode)
                ? "https://api.paddle.com"
                : "https://sandbox-api.paddle.com";
    }
}