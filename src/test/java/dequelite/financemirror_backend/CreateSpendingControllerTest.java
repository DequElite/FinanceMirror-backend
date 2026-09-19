package dequelite.financemirror_backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import dequelite.financemirror_backend.app.analytics.auto_report.AutoReportService;
import dequelite.financemirror_backend.app.spending.create_spending.dto.CreateSpendingRequestDto;
import dequelite.financemirror_backend.domain.profile.ProfileRepository;
import dequelite.financemirror_backend.domain.spending.SpendingRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateSpendingControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private SpendingRepository spendingRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AutoReportService autoReportService;

    private static final String ACCESS_TOKEN =
            "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIyMzVkODg3NC1lNDNiLTRmYzEtYTNlMi05M2ZiYzAwNzExOTIiLCJzdWIiOiI0YTZiYmZlNS0wMGQzLTRhYzAtYTFmMS04NmZlNjY1ZWNmMjAiLCJpYXQiOjE3ODk4MzE2NzgsImV4cCI6MTc4OTgzMjU3OH0.lk0EcL9ug7fk0Sq3Qr3vvb7o4LOVyApyjSKVk-yBvw4";

    @Test
    void createSpending_shouldCreateSpendingAndDecreaseBalance() throws Exception {

        CreateSpendingRequestDto body = createRequest(
                1500,
                "FOOD",
                "Lunch"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(ACCESS_TOKEN);

        HttpEntity<CreateSpendingRequestDto> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<String> response =
                restTemplate.postForEntity(
                        "http://localhost:" + port + "/spending/new",
                        request,
                        String.class
                );

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(
                1,
                spendingRepository.count()
        );

        var spending = spendingRepository.findAll().get(0);

        assertEquals(1500, spending.getPrice());
        assertEquals("Lunch", spending.getDescription());
    }

    @Test
    void createSpending_shouldRejectInvalidPrice() throws Exception {

        CreateSpendingRequestDto body = createRequest(
                -100,
                "FOOD",
                "Lunch"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(ACCESS_TOKEN);

        HttpEntity<CreateSpendingRequestDto> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<String> response =
                restTemplate.postForEntity(
                        "http://localhost:" + port + "/spending/new",
                        request,
                        String.class
                );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );

        assertEquals(0, spendingRepository.count());
    }

    @Test
    void createSpending_shouldRejectBlankDescription() throws Exception {

        CreateSpendingRequestDto body = createRequest(
                1000,
                "FOOD",
                ""
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(ACCESS_TOKEN);

        HttpEntity<CreateSpendingRequestDto> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<String> response =
                restTemplate.postForEntity(
                        "http://localhost:" + port + "/spending/new",
                        request,
                        String.class
                );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );

        assertEquals(0, spendingRepository.count());
    }

    private CreateSpendingRequestDto createRequest(
            int price,
            String category,
            String description
    ) throws Exception {

        String json = """
                {
                    "date": "2026-09-19",
                    "price": %d,
                    "category": "%s",
                    "description": "%s"
                }
                """.formatted(
                price,
                category,
                description
        );

        return objectMapper.readValue(
                json,
                CreateSpendingRequestDto.class
        );
    }
}