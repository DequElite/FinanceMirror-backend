package dequelite.financemirror_backend.app.profile.subscription.create_payment;

import com.paypal.sdk.controllers.OrdersController;
import com.paypal.sdk.exceptions.ApiException;
import com.paypal.sdk.http.response.ApiResponse;
import dequelite.financemirror_backend.domain.subscription.SubscriptionEntity;
import dequelite.financemirror_backend.domain.subscription.SubscriptionRepo;
import dequelite.financemirror_backend.domain.subscription.SubscriptionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.paypal.sdk.models.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CreatePaymentService {

    private final OrdersController ordersController;

    private final SubscriptionRepo subscriptionRepo;

    @Value("${paypal.return-page}")
    private String paypalReturnPage;

    public String createPayment(UUID userID) {
        this.checkUserSubscription(userID);

        return this.createPaymentOrder(userID);
    }

    private void checkUserSubscription(UUID userId){
        Optional<SubscriptionEntity> foundSubscription = this.subscriptionRepo.findByUser_Id(userId);
        if(foundSubscription.isEmpty()) return;

        SubscriptionEntity subscription = foundSubscription.get();

        if(subscription.getStatus().equals(SubscriptionStatus.EXPIRED)) return;

        if(subscription.getStatus() == SubscriptionStatus.ACTIVE && LocalDate.now().isBefore(subscription.getExpires_at())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    private String createPaymentOrder(UUID userId){
        AmountWithBreakdown amountWithBreakdown = new AmountWithBreakdown.Builder(
                "USD",
                "5.00"
        ).build();

        PaymentSource paymentSource = new PaymentSource.Builder()
                .paypal(new PaypalWallet.Builder()
                        .experienceContext(new PaypalWalletExperienceContext.Builder()
                                .returnUrl(this.paypalReturnPage)
                                .cancelUrl(this.paypalReturnPage)
                                .landingPage(PaypalExperienceLandingPage.LOGIN)
                                .userAction(PaypalExperienceUserAction.PAY_NOW)
                                .build())
                        .build())
                .build();


        OrderRequest orderRequest = new OrderRequest.Builder(
                CheckoutPaymentIntent.CAPTURE,
                Collections.singletonList(
                        new PurchaseUnitRequest.Builder(
                                amountWithBreakdown
                        )
                                .description("FinanceMirror Donate")
                                .customId(userId.toString())
                                .build()
                )
        )
                .paymentSource(paymentSource).build();

        CreateOrderInput createOrderInput = new CreateOrderInput.Builder(
                null, orderRequest
        ).build();

        try {
            ApiResponse<Order> response = ordersController.createOrder(createOrderInput);
            List<LinkDescription> links = response.getResult().getLinks();

            return links.stream()
                    .filter(link -> "approve".equalsIgnoreCase(link.getRel()))
                    .findFirst()
                    .map(LinkDescription::getHref)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
        } catch (ApiException | IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
