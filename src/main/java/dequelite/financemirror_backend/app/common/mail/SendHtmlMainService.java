package dequelite.financemirror_backend.app.common.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SendHtmlMainService {

    private final RestClient mailServiceRestClient;

    @Value("${mail-service.api-key}")
    private String apiKey;

    @Value("${mail-service.sender.email}")
    private String senderEmail;

    @Value("${mail-service.sender.name}")
    private String senderName;

    public void sendHtml(
            String recipient,
            String subject,
            String html
    ) {

        log.info("Starting email sending...");
        log.info("Recipient: {}", recipient);
        log.info("Subject: {}", subject);
        log.info("Sender: {} <{}>", senderName, senderEmail);
        log.debug("HTML content length: {} characters", html != null ? html.length() : 0);

        Map<String, Object> body = Map.of(
                "sender", Map.of(
                        "name", senderName,
                        "email", senderEmail
                ),
                "to", List.of(
                        Map.of(
                                "email", recipient
                        )
                ),
                "subject", subject,
                "htmlContent", html
        );

        try {

            log.info("Sending email request to Brevo API...");

            var response = mailServiceRestClient
                    .post()
                    .uri("/v3/smtp/email")
                    .header("api-key", apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();

            log.info(
                    "Email sent successfully. HTTP status: {}",
                    response.getStatusCode()
            );

        } catch (Exception e) {

            log.error(
                    "FAILED TO SEND EMAIL. Recipient: {}, Subject: {}, Sender: {} <{}>",
                    recipient,
                    subject,
                    senderName,
                    senderEmail,
                    e
            );

            throw e;
        }
    }
}