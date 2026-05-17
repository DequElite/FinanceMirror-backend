package dequelite.financemirror_backend.app.analytics.report;

import com.fasterxml.jackson.databind.JsonNode;
import dequelite.financemirror_backend.app.ai.AiService;
import dequelite.financemirror_backend.app.ai.FinancePrompts;
import dequelite.financemirror_backend.app.ai.FinanceRole;
import dequelite.financemirror_backend.app.ai.dto.AiResponseDto;
import dequelite.financemirror_backend.app.analytics.report.dto.AiReportResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AiReportService {
    private final AiService aiService;

    public AiReportResponseDto ask(
            String salary,
            String weeklySpending,
            String remains,
            String daysUntilSalary,
            String expensiveCategory
    ) {
        String prompt = FinancePrompts.financer(
                salary,
                weeklySpending,
                remains,
                daysUntilSalary,
                expensiveCategory
        );

        AiResponseDto response = this.aiService.ask(
                FinanceRole.getRole(),
                prompt
        );
        if(!response.success()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        JsonNode data = response.data();


        return AiReportResponseDto.builder()
                .expenditure_rate(
                        data.path("expenditure_rate").asText()
                )
                .leak_problem(
                        data.path("leak_problem").asText()
                )
                .advice(
                        data.path("advice").asText()
                )
                .build();
    }
}
