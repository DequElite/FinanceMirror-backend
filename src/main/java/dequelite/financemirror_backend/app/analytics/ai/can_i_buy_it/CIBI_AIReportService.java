package dequelite.financemirror_backend.app.analytics.ai.can_i_buy_it;

import com.fasterxml.jackson.databind.JsonNode;
import dequelite.financemirror_backend.app.ai.AiService;
import dequelite.financemirror_backend.app.ai.FinancePrompts;
import dequelite.financemirror_backend.app.ai.FinanceRole;
import dequelite.financemirror_backend.app.ai.dto.AiResponseDto;
import dequelite.financemirror_backend.app.analytics.ai.can_i_buy_it.dto.CIBI_AiReportResponseDto;
import dequelite.financemirror_backend.app.common.response.ApiException;
import dequelite.financemirror_backend.app.common.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CIBI_AIReportService {
    private final AiService aiService;

    public CIBI_AiReportResponseDto ask(
            String language,
            String currency,
            String salary,
            String balance,
            String weeklySpending,
            String spentPercentOfSalary,
            String averageDailySpending,
            String dailyLimit,
            String daysUntilSalary,
            String expensiveCategory,
            String financeResult,
            String description,
            String price,
            String remainingBudget
    ) {
        String prompt = FinancePrompts.canIBuyItHelper(
                language.isBlank() ? "en" : language,
                currency,
                salary,
                balance,
                weeklySpending,
                spentPercentOfSalary,
                averageDailySpending,
                dailyLimit,
                daysUntilSalary,
                expensiveCategory,
                financeResult,
                description,
                price,
                remainingBudget
        );

        AiResponseDto response = this.aiService.ask(
                FinanceRole.getRole(),
                prompt
        );
        if(!response.success()) {
            throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        JsonNode data = response.data();

        return CIBI_AiReportResponseDto.builder()
                .aiResponse(data.path("advice").asText())
                .build();
    }
}
