package dequelite.financemirror_backend.infra.security.filters.SubscriptionFilter;

import dequelite.financemirror_backend.domain.subscription.SubscriptionEntity;
import dequelite.financemirror_backend.domain.subscription.SubscriptionRepo;
import dequelite.financemirror_backend.domain.subscription.SubscriptionStatus;
import dequelite.financemirror_backend.infra.security.processors.AuthProcessor.TokenHeaderDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class SubscriptionFilter extends OncePerRequestFilter {
    private final SubscriptionRepo subscriptionRepo;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        log.info("SubscriptionFilter shouldNotFilter: {}", path);

        return (path.startsWith("/auth") && !path.startsWith("/auth/session"))
                || path.startsWith("/profile/payment")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/webjars");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getServletPath();
        boolean isNonSubscriptionPlace = path.startsWith("/graphql") || path.startsWith("/auth/session");

        log.info("SubscriptionFilter: checking request {}", request.getRequestURI());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.info("SubscriptionFilter: authentication is missing");
            sendForbidden(response);
            return;
        }

        TokenHeaderDTO tokenHeaderDTO = (TokenHeaderDTO) authentication.getPrincipal();
        UUID userId = UUID.fromString(tokenHeaderDTO.getSub());

        log.info("SubscriptionFilter: checking subscription for user {}", userId);

        Optional<SubscriptionEntity> optionalSubscription =
                subscriptionRepo.findByUser_Id(userId);

        if (optionalSubscription.isEmpty()) {
            log.info("SubscriptionFilter: subscription not found for user {}", userId);
            sendForbidden(response);
            return;
        }

        SubscriptionEntity subscription = optionalSubscription.get();

        log.info(
                "SubscriptionFilter: status={}, expiresAt={}",
                subscription.getStatus(),
                subscription.getExpires_at()
        );

        boolean isExpired = subscription.getStatus() == SubscriptionStatus.EXPIRED;

        if (!isExpired && subscription.getExpires_at().isBefore(LocalDate.now())) {
            log.info("SubscriptionFilter: subscription expired today, updating status");
            subscription.setStatus(SubscriptionStatus.EXPIRED);
            subscriptionRepo.save(subscription);
            isExpired = true;
        }

        if (isExpired && !isNonSubscriptionPlace) {
            log.info("SubscriptionFilter: subscription is already expired");
            sendForbidden(response);
            return;
        }

        log.info("SubscriptionFilter: subscription is valid, continuing request");

        filterChain.doFilter(request, response);
    }

    private void sendForbidden(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Unauthorized\"}");
    }
}
