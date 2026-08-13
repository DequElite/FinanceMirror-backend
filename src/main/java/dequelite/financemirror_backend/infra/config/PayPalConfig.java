package dequelite.financemirror_backend.infra.config;

import com.paypal.sdk.Environment;
import com.paypal.sdk.PaypalServerSdkClient;
import com.paypal.sdk.authentication.ClientCredentialsAuthModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PayPalConfig {
    @Value("${paypal.client-id}")
    private String clientId;

    @Value("${paypal.client-secret}")
    private String clientSecret;

    @Value("${paypal.mode}")
    private String mode;

    @Bean
    public PaypalServerSdkClient payPalClient() {
        Environment environment = "live".equalsIgnoreCase(mode)
                ? Environment.PRODUCTION
                : Environment.SANDBOX;

        return new PaypalServerSdkClient.Builder()
                .environment(environment)
                .clientCredentialsAuth(
                        new ClientCredentialsAuthModel.Builder(
                                clientId,
                                clientSecret
                        ).build()
                ).build();
    }
}
