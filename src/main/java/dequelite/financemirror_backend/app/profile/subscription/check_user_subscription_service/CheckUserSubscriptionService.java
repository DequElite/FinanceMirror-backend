package dequelite.financemirror_backend.app.profile.subscription.check_user_subscription_service;

import dequelite.financemirror_backend.domain.subscription.SubscriptionEntity;
import dequelite.financemirror_backend.domain.subscription.SubscriptionRepo;
import dequelite.financemirror_backend.domain.subscription.SubscriptionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckUserSubscriptionService {
    private final SubscriptionRepo subscriptionRepo;

    public void checkUserSubscription(UUID userId) {
        log.info("Checking subscription...");

        Optional<SubscriptionEntity> foundSubscription = this.subscriptionRepo.findByUser_Id(userId);

        if (foundSubscription.isEmpty()) {
            log.info("Subscription not found. User can purchase.");
            return;
        }

        SubscriptionEntity subscription = foundSubscription.get();

        log.info(
                "Subscription found: type={}, status={}, startedAt={}, expiresAt={}",
                subscription.getType(),
                subscription.getStatus(),
                subscription.getStarted_at(),
                subscription.getExpires_at()
        );

        if (subscription.getStatus() == SubscriptionStatus.EXPIRED) {
            log.info("Subscription already expired. User can purchase.");
            return;
        }

        if (subscription.getStatus() == SubscriptionStatus.ACTIVE
                && !LocalDate.now().isAfter(subscription.getExpires_at())) {

            log.warn("User already has active subscription until {}",
                    subscription.getExpires_at());

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Subscription already active"
            );
        }

        log.info("Subscription can be renewed.");
    }
}
