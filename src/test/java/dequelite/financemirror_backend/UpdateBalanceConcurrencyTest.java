package dequelite.financemirror_backend;

import dequelite.financemirror_backend.app.profile.balance.update_balance.dto.UpdateBalanceRequestDto;
import dequelite.financemirror_backend.domain.replenishment.ReplenishmentEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UpdateBalanceConcurrencyTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldHandleConcurrentBalanceUpdates() throws Exception {

        CountDownLatch start = new CountDownLatch(1);

        CompletableFuture<ResponseEntity<String>> request1 =
                CompletableFuture.supplyAsync(() -> {
                    await(start);

                    HttpHeaders headers = new HttpHeaders();
                    headers.setBearerAuth("eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI0MjA3ZjMwYy1kMDY2LTQwNjktYWNmOS0wYjA1YmI3MDAxNTAiLCJzdWIiOiI0YTZiYmZlNS0wMGQzLTRhYzAtYTFmMS04NmZlNjY1ZWNmMjAiLCJpYXQiOjE3ODk4MjgzODEsImV4cCI6MTc4OTgyOTI4MX0.yf22D_bfbwMtH-18uliXzC3b2qLsWuebIG2mZYb-lO0");

                    HttpEntity<UpdateBalanceRequestDto> request =
                            new HttpEntity<>(
                                    UpdateBalanceRequestDto.builder()
                                            .amount(500)
                                            .type(ReplenishmentEnum.ADVANCE)
                                            .date(LocalDate.now())
                                            .description("123")
                                            .build(),
                                    headers
                            );

                    return restTemplate.exchange(
                            "/profile/balance/update",
                            HttpMethod.POST,
                            request,
                            String.class
                    );
                });

        CompletableFuture<ResponseEntity<String>> request2 =
                CompletableFuture.supplyAsync(() -> {
                    await(start);

                    HttpHeaders headers = new HttpHeaders();
                    headers.setBearerAuth("eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI0MjA3ZjMwYy1kMDY2LTQwNjktYWNmOS0wYjA1YmI3MDAxNTAiLCJzdWIiOiI0YTZiYmZlNS0wMGQzLTRhYzAtYTFmMS04NmZlNjY1ZWNmMjAiLCJpYXQiOjE3ODk4MjgzODEsImV4cCI6MTc4OTgyOTI4MX0.yf22D_bfbwMtH-18uliXzC3b2qLsWuebIG2mZYb-lO0");

                    HttpEntity<UpdateBalanceRequestDto> request =
                            new HttpEntity<>(
                                    UpdateBalanceRequestDto.builder()
                                            .amount(500)
                                            .type(ReplenishmentEnum.ADVANCE)
                                            .date(LocalDate.now())
                                            .description("123")
                                            .build(),
                                    headers
                            );

                    return restTemplate.exchange(
                            "/profile/balance/update",
                            HttpMethod.POST,
                            request,
                            String.class
                    );
                });
        start.countDown();

        CompletableFuture.allOf(request1, request2).join();

        System.out.println(request1.join().getBody());
        System.out.println(request2.join().getBody());
    }

    private static void await(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}