package dequelite.financemirror_backend.app.profile.subscription.paypal.payment_webhook;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dequelite.financemirror_backend.app.profile.subscription.paypal.payment_webhook.dto.PaymentWebhookRequestDto;
import dequelite.financemirror_backend.domain.biling.BillingEntity;
import dequelite.financemirror_backend.domain.biling.BillingRepo;
import dequelite.financemirror_backend.domain.biling.BillingStatus;
import dequelite.financemirror_backend.domain.subscription.SubscriptionEntity;
import dequelite.financemirror_backend.domain.subscription.SubscriptionRepo;
import dequelite.financemirror_backend.domain.subscription.SubscriptionStatus;
import dequelite.financemirror_backend.domain.subscription.SubscriptionType;
import dequelite.financemirror_backend.domain.user.UserEntity;
import dequelite.financemirror_backend.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentWebhookService {
    private final BillingRepo billingRepo;
    private final SubscriptionRepo subscriptionRepo;
    private final UserRepository userRepository;

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    @Value("${subscription.expires-at}")
    private Long subscriptionExpiresAt;

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
        log.info("========== PAYPAL WEBHOOK START ==========");
        log.info("Transmission ID: {}", transmissionId);
        log.info("Transmission Time: {}", transmissionTime);
        log.info("Auth Algo: {}", authAlgo);
        log.info("Cert URL: {}", certUrl);

        boolean isValid = verifyWebhookSignature(
                transmissionId,
                transmissionTime,
                transmissionSig,
                certUrl,
                authAlgo,
                rawBody
        );

        log.info("Webhook verification result: {}", isValid);

        if (!isValid) {
            log.warn("Webhook signature is INVALID");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        log.info("Webhook signature verified successfully");

        proccessPaymentWebHook(rawBody);

        log.info("========== PAYPAL WEBHOOK END ==========");
    }

    private void proccessPaymentWebHook(String rawBody) {

        log.info("Processing webhook event...");

        try {

            JsonNode rootNode = objectMapper.readTree(rawBody);

            PaymentWebhookRequestDto dto =
                    objectMapper.treeToValue(rootNode, PaymentWebhookRequestDto.class);

            String eventType = dto.getEventType();

            log.info("Event Type: {}", eventType);

            JsonNode resource = dto.getResource();

            switch (eventType) {

                case "PAYMENT.CAPTURE.COMPLETED":
                    log.info("Payment capture completed event received");
                    handleSubscriptionActivated(resource);
                    break;

                case "BILLING.SUBSCRIPTION.CANCELLED":
                    log.info("Subscription cancelled event received");
                    handleSubscriptionCancelled(resource);
                    break;

                default:
                    log.warn("Unhandled PayPal event: {}", eventType);
            }

        } catch (Exception e) {

            log.error("Webhook processing failed", e);

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private UserEntity findUserFromWebhook(JsonNode resource) {

        log.info("Searching user from webhook...");

        String customId =
                resource.has("custom_id")
                        ? resource.get("custom_id").asText()
                        : null;

        log.info("custom_id: {}", customId);

        if (customId == null || customId.isBlank()) {

            log.error("custom_id is missing");

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Missing custom_id in webhook"
            );
        }

        UUID userId = UUID.fromString(customId);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> {

                    log.error("User not found: {}", userId);

                    return new ResponseStatusException(HttpStatus.NOT_FOUND);
                });

        log.info("User found: {}", user.getId());

        return user;
    }

    private void handleSubscriptionActivated(JsonNode resource) {

        log.info("===== HANDLE PAYMENT CAPTURE COMPLETED =====");

        String captureId = resource.get("id").asText();

        log.info("PayPal Capture ID: {}", captureId);

        Optional<BillingEntity> foundBilling =
                this.billingRepo.findByPaypalCaptureId(captureId);

        if (foundBilling.isPresent()) {
            log.warn("Billing already exists. Skip webhook.");
            return;
        }

        UserEntity user = this.findUserFromWebhook(resource);

        log.info("User found: {}", user.getId());

        SubscriptionEntity subscription =
                this.subscriptionRepo.findByUser_Id(user.getId())
                        .orElseGet(() -> {
                            SubscriptionEntity newSubscription = new SubscriptionEntity();
                            newSubscription.setUser(user);
                            return newSubscription;
                        });

        subscription.setType(SubscriptionType.PREMIUM);
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setStarted_at(LocalDate.now());
        subscription.setUpdated_at(LocalDate.now());
        subscription.setExpires_at(LocalDate.now().plusDays(subscriptionExpiresAt));

        this.subscriptionRepo.save(subscription);

        double amount = resource.get("amount").get("value").asDouble();
        String currency = resource.get("amount").get("currency_code").asText();

        String orderId =
                resource.has("supplementary_data")
                        && resource.get("supplementary_data").has("related_ids")
                        ? resource.get("supplementary_data").get("related_ids").get("order_id").asText(captureId)
                        : captureId;

        BillingEntity billing = new BillingEntity();

        billing.setUser(user);
        billing.setSubscription(subscription);
        billing.setPaypalOrderId(orderId);
        billing.setPaypalCaptureId(captureId);
        billing.setPaypalResponse(resource);
        billing.setAmount(java.math.BigDecimal.valueOf(amount));
        billing.setCurrency(currency);
        billing.setStatus(BillingStatus.COMPLETED);

        this.billingRepo.save(billing);

        log.info("Billing saved successfully.");
        log.info("===== PAYMENT CAPTURE COMPLETE =====");
    }


    private void handleSubscriptionCancelled(JsonNode resource) {

        log.info("===== HANDLE SUBSCRIPTION CANCELLED =====");

        String paypalSubscriptionId = resource.get("id").asText();

        log.info("PayPal Subscription ID: {}", paypalSubscriptionId);

        Optional<BillingEntity> foundBilling =
                this.billingRepo.findByPaypalCaptureId(paypalSubscriptionId);

        if (foundBilling.isPresent()) {
            log.warn("Billing already exists. Skip webhook.");
            throw new ResponseStatusException(HttpStatus.OK);
        }

        UserEntity user = this.findUserFromWebhook(resource);

        log.info("User found: {}", user.getId());

        double amount = 0.0;
        String currency = "USD";

        if (resource.has("billing_info")
                && resource.get("billing_info").has("last_payment")) {

            JsonNode lastPayment = resource.get("billing_info").get("last_payment");

            amount = lastPayment.get("amount").get("value").asDouble();
            currency = lastPayment.get("amount").get("currency_code").asText();
        }

        log.info("Payment amount: {} {}", amount, currency);

        BillingEntity billing = new BillingEntity();

        billing.setUser(user);
        billing.setSubscription(null);

        String transactionId =
                resource.has("billing_info")
                        && resource.get("billing_info").has("last_payment")
                        ? resource.get("billing_info")
                        .get("last_payment")
                        .get("amount")
                        .get("id")
                        .asText(paypalSubscriptionId)
                        : paypalSubscriptionId;

        billing.setPaypalOrderId(paypalSubscriptionId);
        billing.setPaypalCaptureId(transactionId);
        billing.setPaypalResponse(resource);
        billing.setAmount(java.math.BigDecimal.valueOf(amount));
        billing.setCurrency(currency);
        billing.setStatus(BillingStatus.FAILED);

        this.billingRepo.save(billing);

        log.info("Cancellation billing saved.");
        log.info("===== SUBSCRIPTION CANCELLED COMPLETE =====");
    }

    private boolean verifyWebhookSignature(
            String transmissionId,
            String transmissionTime,
            String transmissionSig,
            String certUrl,
            String authAlgo,
            String rawBody
    ) {

        log.info("Verifying PayPal webhook signature...");

        try {

            String baseUrl =
                    "live".equalsIgnoreCase(mode)
                            ? "https://api-m.paypal.com"
                            : "https://api-m.sandbox.paypal.com";

            log.info("Base URL: {}", baseUrl);

            String accessToken = getAccessToken(baseUrl);

            log.info("Access token received");

            Map<?, ?> webhookEventMap =
                    objectMapper.readValue(rawBody, Map.class);

            Map<String, Object> verificationPayload = Map.of(
                    "auth_algo", authAlgo,
                    "cert_url", certUrl,
                    "transmission_id", transmissionId,
                    "transmission_sig", transmissionSig,
                    "transmission_time", transmissionTime,
                    "webhook_id", paypalWebhookId,
                    "webhook_event", webhookEventMap
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);

            HttpEntity<Map<String, Object>> entity =
                    new HttpEntity<>(verificationPayload, headers);

            String url =
                    baseUrl + "/v1/notifications/verify-webhook-signature";

            log.info("Calling PayPal verification endpoint");

            ResponseEntity<JsonNode> response =
                    restTemplate.postForEntity(url, entity, JsonNode.class);

            log.info("Verification HTTP Status: {}", response.getStatusCode());

            if (response.getBody() != null) {

                log.info("Verification response: {}", response.getBody());

                String status =
                        response.getBody()
                                .path("verification_status")
                                .asText();

                log.info("Verification status: {}", status);

                return "SUCCESS".equalsIgnoreCase(status);
            }

            log.warn("Verification response body is empty");

            return false;

        } catch (Exception e) {

            log.error("Webhook verification failed", e);

            return false;
        }
    }

    private String getAccessToken(String baseUrl) {

        log.info("Requesting PayPal access token...");

        String auth = clientId + ":" + clientSecret;

        String encodedAuth =
                Base64.getEncoder()
                        .encodeToString(auth.getBytes(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Basic " + encodedAuth);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body =
                new LinkedMultiValueMap<>();

        body.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> entity =
                new HttpEntity<>(body, headers);

        String url = baseUrl + "/v1/oauth2/token";

        ResponseEntity<JsonNode> response =
                restTemplate.postForEntity(url, entity, JsonNode.class);

        log.info("Access token HTTP Status: {}", response.getStatusCode());

        if (response.getBody() != null &&
                response.getBody().has("access_token")) {

            log.info("Access token received successfully");

            return response.getBody()
                    .get("access_token")
                    .asText();
        }

        log.error("Failed to obtain access token");

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }
}
