package dequelite.financemirror_backend.app.profile.subscription.paddle.create_payment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.sdk.controllers.OrdersController;

import com.paypal.sdk.http.response.ApiResponse;
import com.paypal.sdk.models.*;
import dequelite.financemirror_backend.app.common.response.ApiException;
import dequelite.financemirror_backend.app.common.response.ErrorCode;
import dequelite.financemirror_backend.app.profile.subscription.check_user_subscription_service.CheckUserSubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreatePaymentService {

    private final CheckUserSubscriptionService checkUserSubscriptionService;
    private final RestTemplate restTemplate;
    private final String paddleBaseUrl;

    @Value("${paddle.api-key}")
    private String apiKey;

    @Value("${paddle.price-id}")
    private String priceId;
    public String createPayment(UUID userId) {
        log.info("=== CREATE PAYMENT START ===");
        log.info("User ID: {}", userId);

        this.checkUserSubscriptionService.checkUserSubscription(userId);

        String transactionId = this.createPaddleTransaction(userId);

        log.info("Payment created successfully. Transaction ID: {}", transactionId);
        log.info("=== CREATE PAYMENT END ===");

        return transactionId;
    }

    private String createPaddleTransaction(UUID userId) {
        log.info("Creating Paddle transaction...");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = Map.of(
                "items", List.of(
                        Map.of(
                                "price_id", priceId,
                                "quantity", 1
                        )
                ),
                "custom_data", Map.of(
                        "userId", userId.toString()
                )
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        String url = paddleBaseUrl + "/transactions";

        try {
            ResponseEntity<JsonNode> response =
                    restTemplate.postForEntity(url, entity, JsonNode.class);

            log.info("Paddle responded. HTTP Status: {}", response.getStatusCode());

            JsonNode responseBody = response.getBody();

            if (responseBody == null || !responseBody.has("data")) {
                log.error("Paddle response missing 'data' field");
                throw new ApiException(
                        ErrorCode.PADDLE_INVALID_RESPONSE
                );
            }

            String transactionId = responseBody.get("data").get("id").asText();

            log.info("Paddle transaction created: {}", transactionId);

            return transactionId;

        } catch (Exception e) {
            log.error("========== PADDLE API EXCEPTION ==========");
            log.error("Message: {}", e.getMessage(), e);

            throw new ApiException(
                    ErrorCode.PADDLE_PAYMENT_CREATION_FAILED
            );
        }
    }
}