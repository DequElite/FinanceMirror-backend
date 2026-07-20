package dequelite.financemirror_backend.app.profile.subscription.capture_payment;

import com.paypal.sdk.controllers.OrdersController;
import com.paypal.sdk.exceptions.ApiException;
import com.paypal.sdk.http.response.ApiResponse;
import com.paypal.sdk.models.CaptureOrderInput;
import com.paypal.sdk.models.Order;
import dequelite.financemirror_backend.app.profile.subscription.check_user_subscription_service.CheckUserSubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CapturePaymentService {
    private final CheckUserSubscriptionService checkUserSubscriptionService;
    private final OrdersController ordersController;

    public void capture(String token, UUID userId){
        this.checkUserSubscriptionService.checkUserSubscription(userId);

        CaptureOrderInput captureOrderInput = new CaptureOrderInput.Builder(
                token,
                null
        ).build();

        try {
            ApiResponse<Order> response = ordersController.captureOrder(captureOrderInput);

            Order order = response.getResult();

            if (!"COMPLETED".equals(order.getStatus())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Payment was not completed"
                );
            }
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Capture failed"
            );
        }
    }
}
