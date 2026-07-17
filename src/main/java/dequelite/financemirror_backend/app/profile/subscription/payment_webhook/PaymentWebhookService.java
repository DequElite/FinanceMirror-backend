package dequelite.financemirror_backend.app.profile.subscription.payment_webhook;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dequelite.financemirror_backend.app.profile.subscription.payment_webhook.dto.PaymentWebhookRequestDto;
import dequelite.financemirror_backend.domain.biling.BillingEntity;
import dequelite.financemirror_backend.domain.biling.BillingRepo;
import dequelite.financemirror_backend.domain.biling.BillingStatus;
import dequelite.financemirror_backend.domain.subscription.SubscriptionEntity;
import dequelite.financemirror_backend.domain.subscription.SubscriptionRepo;
import dequelite.financemirror_backend.domain.subscription.SubscriptionStatus;
import dequelite.financemirror_backend.domain.user.UserEntity;
import dequelite.financemirror_backend.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentWebhookService {
    private final BillingRepo billingRepo;
    private final SubscriptionRepo subscriptionRepo;
    private final UserRepository userRepository;

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    @Value("${subscription.expires-at}")
    private Integer subscriptionExpiresAt;

    @Value("${paypal.client-id}")
    private String clientId;

    @Value("${paypal.client-secret}")
    private String clientSecret;

    @Value("${paypal.mode:sandbox}")
    private String mode;

    @Value("${paypal.webhook-id}")
    private String paypalWebhookId;

    @Transactional
    public void completeSubscription(
            String transmissionId,
            String transmissionTime,
            String transmissionSig,
            String certUrl,
            String authAlgo,
            String rawBody
    ) {
        boolean isValid = this.verifyWebhookSignature(transmissionId, transmissionTime, transmissionSig, certUrl, authAlgo, rawBody);
        if(!isValid) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        this.proccessPaymentWebHook(rawBody);
    }

    private void proccessPaymentWebHook(String rawBody){
        try {
            JsonNode rootNode = objectMapper.readTree(rawBody);

            PaymentWebhookRequestDto dto = objectMapper.treeToValue(rootNode, PaymentWebhookRequestDto.class);

            String eventType = dto.getEventType();
            JsonNode resource = dto.getResource();

            switch (eventType) {
                case "BILLING.SUBSCRIPTION.ACTIVATED":
                    handleSubscriptionActivated(resource);
                    break;

                case "BILLING.SUBSCRIPTION.CANCELLED":
                    handleSubscriptionCancelled(resource);
                    break;

                default:
                    break;
            }

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private UserEntity findUserFromWebhook(JsonNode resource) {
        String customId = resource.has("custom_id") ? resource.get("custom_id").asText() : null;

        if (customId == null || customId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing custom_id in webhook");
        }

        UUID userId = UUID.fromString(customId);
        UserEntity user = this.userRepository
                .findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return user;
    }

    private void handleSubscriptionActivated(JsonNode resource) {
        String paypalSubscriptionId = resource.get("id").asText();

        Optional<BillingEntity> foundBilling = this.billingRepo.findByPaypal_Capture_Id(paypalSubscriptionId);
        if(foundBilling.isPresent()) {
            throw new ResponseStatusException(HttpStatus.OK);
        }

        UserEntity user = this.findUserFromWebhook(resource);

        SubscriptionEntity subscription = this.subscriptionRepo.findByUser_Id(user.getId())
                .orElseGet(() -> {
                    SubscriptionEntity newSubscription = new SubscriptionEntity();
                    newSubscription.setUser(user);
                    newSubscription.setStatus(SubscriptionStatus.ACTIVE);
                    newSubscription.setStarted_at(LocalDate.now());
                    newSubscription.setUpdated_at(LocalDate.now());
                    newSubscription.setExpires_at(LocalDate.now().plus(Duration.ofMillis(subscriptionExpiresAt)));

                    return this.subscriptionRepo.save(newSubscription);
                });

        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setStarted_at(LocalDate.now());
        subscription.setUpdated_at(LocalDate.now());
        subscription.setExpires_at(LocalDate.now().plus(Duration.ofMillis(subscriptionExpiresAt)));

        double amount = 0.0;
        String currency = "USD";

        if (resource.has("billing_info") && resource.get("billing_info").has("last_payment")) {
            JsonNode lastPayment = resource.get("billing_info").get("last_payment");
            amount = lastPayment.get("amount").get("value").asDouble();
            currency = lastPayment.get("amount").get("currency_code").asText();
        }

        BillingEntity billing = new BillingEntity();

        billing.setUser(user);
        billing.setSubscription(subscription);

        String transactionId = resource.has("billing_info") && resource.get("billing_info").has("last_payment")
                ? resource.get("billing_info").get("last_payment").get("amount").get("id").asText(paypalSubscriptionId)
                : paypalSubscriptionId;
        billing.setPaypalOrderId(paypalSubscriptionId);
        billing.setPaypalCaptureId(transactionId);
        billing.setPaypalResponse(resource);
        billing.setAmount(java.math.BigDecimal.valueOf(amount));
        billing.setCurrency(currency);
        billing.setStatus(BillingStatus.COMPLETED);

        this.billingRepo.save(billing);
    }


    private void handleSubscriptionCancelled(JsonNode resource) {
        String paypalSubscriptionId = resource.get("id").asText();

        Optional<BillingEntity> foundBilling = this.billingRepo.findByPaypal_Capture_Id(paypalSubscriptionId);
        if(foundBilling.isPresent()) {
            throw new ResponseStatusException(HttpStatus.OK);
        }

        UserEntity user = this.findUserFromWebhook(resource);

        double amount = 0.0;
        String currency = "USD";

        if (resource.has("billing_info") && resource.get("billing_info").has("last_payment")) {
            JsonNode lastPayment = resource.get("billing_info").get("last_payment");
            amount = lastPayment.get("amount").get("value").asDouble();
            currency = lastPayment.get("amount").get("currency_code").asText();
        }

        BillingEntity billing = new BillingEntity();

        billing.setUser(user);
        billing.setSubscription(null);

        String transactionId = resource.has("billing_info") && resource.get("billing_info").has("last_payment")
                ? resource.get("billing_info").get("last_payment").get("amount").get("id").asText(paypalSubscriptionId)
                : paypalSubscriptionId;
        billing.setPaypalOrderId(paypalSubscriptionId);
        billing.setPaypalCaptureId(transactionId);
        billing.setPaypalResponse(resource);
        billing.setAmount(java.math.BigDecimal.valueOf(amount));
        billing.setCurrency(currency);
        billing.setStatus(BillingStatus.FAILED);

        this.billingRepo.save(billing);
    }

    private boolean verifyWebhookSignature(
            String transmissionId,
            String transmissionTime,
            String transmissionSig,
            String certUrl,
            String authAlgo,
            String rawBody
    ) {
        try {
            String baseUrl = "live".equalsIgnoreCase(mode)
                    ? "https://api-m.paypal.com"
                    : "https://api-m.sandbox.paypal.com";

            String accessToken = getAccessToken(baseUrl);

            Map<?, ?> webhookEventMap = objectMapper.readValue(rawBody, Map.class);

            Map<String, Object> verificationPayload = Map.of(
                    "auth_algo", authAlgo,
                    "cert_url", certUrl,
                    "transmission_id", transmissionId,
                    "transmission_sig", transmissionSig,
                    "transmission_time", transmissionTime,
                    "webhook_id", this.paypalWebhookId,
                    "webhook_event", webhookEventMap
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(verificationPayload, headers);

            String url = baseUrl + "/v1/notifications/verify-webhook-signature";
            ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity(url, entity, JsonNode.class);

            if (responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody() != null) {
                JsonNode responseBody = responseEntity.getBody();
                if (responseBody.has("verification_status")) {
                    String status = responseBody.get("verification_status").asText();
                    return "SUCCESS".equalsIgnoreCase(status);
                }
            }

            return false;

        } catch (JsonProcessingException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private String getAccessToken(String baseUrl) {
        String auth = clientId + ":" + clientSecret;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Basic " + encodedAuth);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        String url = baseUrl + "/v1/oauth2/token";
        ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity(url, entity, JsonNode.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody() != null) {
            JsonNode responseBody = responseEntity.getBody();
            if (responseBody.has("access_token")) {
                return responseBody.get("access_token").asText();
            }
        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }
}
