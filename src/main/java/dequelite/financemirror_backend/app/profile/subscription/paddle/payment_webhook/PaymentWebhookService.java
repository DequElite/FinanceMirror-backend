package dequelite.financemirror_backend.app.profile.subscription.paddle.payment_webhook;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dequelite.financemirror_backend.app.common.response.ApiException;
import dequelite.financemirror_backend.app.common.response.ErrorCode;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HexFormat;
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

    @Value("${subscription.expires-at}")
    private Long subscriptionExpiresAt;

    @Value("${paddle.webhook-secret}")
    private String paddleWebhookSecret;

    @Transactional
    public void completeSubscription(String paddleSignature, String rawBody) {
        log.info("========== PADDLE WEBHOOK START ==========");

        boolean isValid = verifyWebhookSignature(paddleSignature, rawBody);

        log.info("Webhook verification result: {}", isValid);

        if (!isValid) {
            log.warn("Webhook signature is INVALID");
            throw new ApiException(
                    ErrorCode.PADDLE_WEBHOOK_INVALID_SIGNATURE
            );
        }

        log.info("Webhook signature verified successfully");

        processPaymentWebhook(rawBody);

        log.info("========== PADDLE WEBHOOK END ==========");
    }

    private void processPaymentWebhook(String rawBody) {
        log.info("Processing webhook event...");

        try {
            JsonNode rootNode = objectMapper.readTree(rawBody);

            String eventType = rootNode.get("event_type").asText();

            log.info("Event Type: {}", eventType);

            JsonNode data = rootNode.get("data");

            switch (eventType) {

                case "transaction.completed":
                    log.info("Transaction completed event received");
                    handleSubscriptionActivated(data);
                    break;

                case "subscription.canceled":
                    log.info("Subscription cancelled event received");
                    handleSubscriptionCancelled(data);
                    break;

                default:
                    log.warn("Unhandled Paddle event: {}", eventType);
            }

        } catch (Exception e) {
            log.error("Webhook processing failed", e);
            throw new ApiException(
                    ErrorCode.PADDLE_WEBHOOK_PROCESSING_FAILED
            );
        }
    }

    private UserEntity findUserFromWebhook(JsonNode data) {
        log.info("Searching user from webhook...");

        JsonNode customData = data.get("custom_data");

        String userIdRaw =
                customData != null && customData.has("userId")
                        ? customData.get("userId").asText()
                        : null;

        log.info("userId from custom_data: {}", userIdRaw);

        if (userIdRaw == null || userIdRaw.isBlank()) {
            log.error("custom_data.userId is missing");
            throw new ApiException(
                    ErrorCode.PADDLE_WEBHOOK_INVALID_DATA
            );
        }

        UUID userId = UUID.fromString(userIdRaw);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found: {}", userId);
                    return new ApiException(
                            ErrorCode.USER_NOT_FOUND
                    );
                });

        log.info("User found: {}", user.getId());

        return user;
    }

    private void handleSubscriptionActivated(JsonNode data) {
        log.info("===== HANDLE TRANSACTION COMPLETED =====");

        String transactionId = data.get("id").asText();

        log.info("Paddle Transaction ID: {}", transactionId);

        Optional<BillingEntity> foundBilling =
                this.billingRepo.findByPaypalCaptureId(transactionId);

        if (foundBilling.isPresent()) {
            log.warn("Billing already exists. Skip webhook.");
            return;
        }

        UserEntity user = this.findUserFromWebhook(data);

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

        JsonNode totals = data.get("details").get("totals");
        double amount = Double.parseDouble(totals.get("total").asText()) / 100.0;
        String currency = totals.get("currency_code").asText();

        BillingEntity billing = new BillingEntity();

        billing.setUser(user);
        billing.setSubscription(subscription);
        billing.setPaypalOrderId(transactionId);
        billing.setPaypalCaptureId(transactionId);
        billing.setPaypalResponse(data);
        billing.setAmount(java.math.BigDecimal.valueOf(amount));
        billing.setCurrency(currency);
        billing.setStatus(BillingStatus.COMPLETED);

        this.billingRepo.save(billing);

        log.info("Billing saved successfully.");
        log.info("===== TRANSACTION COMPLETE =====");
    }

    private void handleSubscriptionCancelled(JsonNode data) {
        log.info("===== HANDLE SUBSCRIPTION CANCELLED =====");

        String paddleSubscriptionId = data.get("id").asText();

        log.info("Paddle Subscription ID: {}", paddleSubscriptionId);

        Optional<BillingEntity> foundBilling =
                this.billingRepo.findByPaypalCaptureId(paddleSubscriptionId);

        if (foundBilling.isPresent()) {
            log.warn("Billing already exists. Skip webhook.");
            return;
        }

        UserEntity user = this.findUserFromWebhook(data);

        log.info("User found: {}", user.getId());

        BillingEntity billing = new BillingEntity();

        billing.setUser(user);
        billing.setSubscription(null);
        billing.setPaypalOrderId(paddleSubscriptionId);
        billing.setPaypalCaptureId(paddleSubscriptionId);
        billing.setPaypalResponse(data);
        billing.setAmount(java.math.BigDecimal.ZERO);
        billing.setCurrency("USD");
        billing.setStatus(BillingStatus.FAILED);

        this.billingRepo.save(billing);

        log.info("Cancellation billing saved.");
        log.info("===== SUBSCRIPTION CANCELLED COMPLETE =====");
    }

    private boolean verifyWebhookSignature(String paddleSignature, String rawBody) {
        log.info("Verifying Paddle webhook signature...");

        try {
            String ts = null;
            String h1 = null;

            for (String part : paddleSignature.split(";")) {
                String[] kv = part.split("=", 2);
                if (kv.length != 2) continue;
                if ("ts".equals(kv[0])) ts = kv[1];
                if ("h1".equals(kv[0])) h1 = kv[1];
            }

            if (ts == null || h1 == null) {
                log.error("Malformed Paddle-Signature header: {}", paddleSignature);
                return false;
            }

            String signedPayload = ts + ":" + rawBody;

            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(
                    paddleWebhookSecret.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256"
            ));

            byte[] hash = mac.doFinal(signedPayload.getBytes(StandardCharsets.UTF_8));
            String computedHex = HexFormat.of().formatHex(hash);

            boolean matches = computedHex.equalsIgnoreCase(h1);

            log.info("Signature match: {}", matches);

            return matches;

        } catch (Exception e) {
            log.error("Webhook verification failed", e);
            return false;
        }
    }
}