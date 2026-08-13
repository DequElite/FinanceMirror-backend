package dequelite.financemirror_backend.infra.config;

import com.paypal.sdk.PaypalServerSdkClient;
import com.paypal.sdk.controllers.OrdersController;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ComponentConfig {

    @Value("${mail-service.domain}")
    private String mailServiceDomain;

    @Bean
    public ChatClient chatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }

    @Bean
    public OrdersController ordersController(PaypalServerSdkClient payPalClient) {
        return payPalClient.getOrdersController();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public RestClient mailServiceRestClient() {
        return RestClient.builder()
                .baseUrl(mailServiceDomain)
                .build();
    }
}
