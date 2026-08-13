package dequelite.financemirror_backend.domain.dto;

import dequelite.financemirror_backend.domain.subscription.SubscriptionStatus;
import dequelite.financemirror_backend.domain.subscription.SubscriptionType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
public class SubscriptionDto {
    private UUID id;

    private SubscriptionType type;

    private SubscriptionStatus status;

    private LocalDate created_at;

    private LocalDate started_at;

    private LocalDate updated_at;

    private LocalDate expires_at;
}
