package dequelite.financemirror_backend.app.profile.subscription.check_user_subscription_service;

import dequelite.financemirror_backend.app.common.response.ApiException;
import dequelite.financemirror_backend.app.common.response.ErrorCode;
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
        Optional<SubscriptionEntity> foundSubscription = this.subscriptionRepo.findByUser_Id(userId);

        if (foundSubscription.isEmpty()) {
            return;
        }

        SubscriptionEntity subscription = foundSubscription.get();

        if (subscription.getStatus() == SubscriptionStatus.EXPIRED) {
            return;
        }

        if (subscription.getStatus() == SubscriptionStatus.ACTIVE
                && !LocalDate.now().isAfter(subscription.getExpires_at())) {
            throw new ApiException(
                    ErrorCode.SUBSCRIPTION_ALREADY_ACTIVE
            );
        }
    }
}
