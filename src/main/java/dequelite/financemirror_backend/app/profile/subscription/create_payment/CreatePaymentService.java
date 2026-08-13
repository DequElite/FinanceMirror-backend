package dequelite.financemirror_backend.app.profile.subscription.create_payment;

import com.paypal.sdk.controllers.OrdersController;
import com.paypal.sdk.exceptions.ApiException;
import com.paypal.sdk.http.response.ApiResponse;
import com.paypal.sdk.models.*;
import dequelite.financemirror_backend.app.profile.subscription.check_user_subscription_service.CheckUserSubscriptionService;
import dequelite.financemirror_backend.domain.subscription.SubscriptionEntity;
import dequelite.financemirror_backend.domain.subscription.SubscriptionRepo;
import dequelite.financemirror_backend.domain.subscription.SubscriptionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreatePaymentService {

    private final OrdersController ordersController;
    private final CheckUserSubscriptionService checkUserSubscriptionService;

    @Value("${paypal.return-page}")
    private String paypalReturnPage;

    public String createPayment(UUID userId) {
        log.info("=== CREATE PAYMENT START ===");
        log.info("User ID: {}", userId);

        this.checkUserSubscriptionService.checkUserSubscription(userId);

        String approveUrl = this.createPaymentOrder(userId);

        log.info("Payment created successfully");
        log.info("Approve URL: {}", approveUrl);
        log.info("=== CREATE PAYMENT END ===");

        return approveUrl;
    }

    private String createPaymentOrder(UUID userId) {
        log.info("Creating PayPal order...");
        log.info("Return page: {}", paypalReturnPage);

        AmountWithBreakdown amountWithBreakdown = new AmountWithBreakdown.Builder(
                "USD",
                "5.00"
        ).build();

        PaymentSource paymentSource = new PaymentSource.Builder()
                .paypal(
                        new PaypalWallet.Builder()
                                .experienceContext(
                                        new PaypalWalletExperienceContext.Builder()
                                                .returnUrl(paypalReturnPage)
                                                .cancelUrl(paypalReturnPage)
                                                .landingPage(PaypalExperienceLandingPage.LOGIN)
                                                .userAction(PaypalExperienceUserAction.PAY_NOW)
                                                .build()
                                )
                                .build()
                )
                .build();

        OrderRequest orderRequest = new OrderRequest.Builder(
                CheckoutPaymentIntent.CAPTURE,
                Collections.singletonList(
                        new PurchaseUnitRequest.Builder(amountWithBreakdown)
                                .description("FinanceMirror Premium")
                                .customId(userId.toString())
                                .build()
                )
        )
                .paymentSource(paymentSource)
                .build();

        CreateOrderInput createOrderInput = new CreateOrderInput.Builder(
                null,
                orderRequest
        ).build();

        try {
            log.info("Sending request to PayPal...");

            ApiResponse<Order> response = ordersController.createOrder(createOrderInput);

            log.info("PayPal responded successfully.");
            log.info("HTTP Status: {}", response.getStatusCode());
            log.info("Order ID: {}", response.getResult().getId());

            List<LinkDescription> links = response.getResult().getLinks();

            log.info("========== PAYPAL RESPONSE ==========");

            Order order = response.getResult();

            log.info("Order ID: {}", order.getId());
            log.info("Status: {}", order.getStatus());

            if (order.getLinks() == null) {
                log.error("Links = NULL");
            } else {
                for (LinkDescription link : order.getLinks()) {
                    log.info("--------------------------------");
                    log.info("rel    = {}", link.getRel());
                    log.info("href   = {}", link.getHref());
                    log.info("method = {}", link.getMethod());
                }
            }

            log.info("====================================");

            String approveUrl = links.stream()
                    .filter(link ->
                            "payer-action".equalsIgnoreCase(link.getRel()) ||
                                    "approve".equalsIgnoreCase(link.getRel()))
                    .findFirst()
                    .map(LinkDescription::getHref)
                    .orElseThrow(() -> {
                        log.error("Approve URL not found in PayPal response!");
                        return new ResponseStatusException(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "Approve URL not found"
                        );
                    });

            log.info("Approve URL found successfully.");

            return approveUrl;

        } catch (ApiException e) {
            log.error("========== PAYPAL API EXCEPTION ==========");
            log.error("Status Code: {}", e.getResponseCode());
            log.error("Message: {}", e.getMessage(), e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "PayPal API Error"
            );

        } catch (IOException e) {
            log.error("========== IO EXCEPTION ==========");
            log.error("Message: {}", e.getMessage(), e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "IO Error"
            );

        } catch (Exception e) {
            log.error("========== UNKNOWN EXCEPTION ==========");
            log.error("Message: {}", e.getMessage(), e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unexpected Error"
            );
        }
    }
}