package dequelite.financemirror_backend.app.profile.subscription.paypal.payment_webhook.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;

@Getter
public class PaymentWebhookRequestDto {
    private String id;

    @JsonProperty("event_type")
    private String eventType;

    private JsonNode resource;
}
