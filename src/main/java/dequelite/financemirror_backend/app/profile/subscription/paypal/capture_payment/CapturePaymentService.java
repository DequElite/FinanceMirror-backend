package dequelite.financemirror_backend.app.profile.subscription.paypal.capture_payment;

import com.paypal.sdk.controllers.OrdersController;
import com.paypal.sdk.http.response.ApiResponse;
import com.paypal.sdk.models.CaptureOrderInput;
import com.paypal.sdk.models.Order;
import com.paypal.sdk.models.OrderStatus;
import dequelite.financemirror_backend.app.profile.subscription.check_user_subscription_service.CheckUserSubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CapturePaymentService {

    private final CheckUserSubscriptionService checkUserSubscriptionService;
    private final OrdersController ordersController;

    public void capture(String token, UUID userId) {

        log.info("========== CAPTURE PAYMENT START ==========");
        log.info("User ID: {}", userId);
        log.info("Order Token: {}", token);

        this.checkUserSubscriptionService.checkUserSubscription(userId);

        CaptureOrderInput captureOrderInput = new CaptureOrderInput.Builder(
                token,
                null
        ).build();

        try {
            log.info("Sending capture request to PayPal...");

            log.info("Before PayPal capture");
            ApiResponse<Order> response = ordersController.captureOrder(captureOrderInput);
            log.info("After PayPal capture");

            log.info("PayPal responded successfully.");
            log.info("HTTP Status: {}", response.getStatusCode());

            Order order = response.getResult();

            log.info("Order ID: {}", order.getId());
            log.info("Order Status: {}", order.getStatus());

            if (order.getStatus() != OrderStatus.COMPLETED) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Payment was not completed"
                );
            }

            log.info("Payment captured successfully.");
            log.info("Waiting for PayPal webhook...");
            log.info("========== CAPTURE PAYMENT END ==========");

        } catch (Exception e) {

            log.error("========== CAPTURE PAYMENT FAILED ==========");
            log.error("User ID: {}", userId);
            log.error("Order Token: {}", token);
            log.error("Message: {}", e.getMessage(), e);

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Capture failed"
            );
        }
    }
}