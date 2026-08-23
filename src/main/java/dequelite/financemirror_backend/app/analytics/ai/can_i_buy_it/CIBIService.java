package dequelite.financemirror_backend.app.analytics.ai.can_i_buy_it;

import dequelite.financemirror_backend.app.analytics.ai.ai_report.dto.AiReportResponseDto;
import dequelite.financemirror_backend.app.analytics.ai.ai_report.dto.AnalyzedDto;
import dequelite.financemirror_backend.app.analytics.ai.can_i_buy_it.dto.CIBIRequestDto;
import dequelite.financemirror_backend.app.analytics.ai.can_i_buy_it.dto.CIBIResponseDto;
import dequelite.financemirror_backend.app.analytics.ai.can_i_buy_it.dto.CIBI_AiReportResponseDto;
import dequelite.financemirror_backend.app.analytics.analysis.AnalyzeUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CIBIService {
    private final AnalyzeUserService analyzeUserService;
    private final CIBI_AIReportService cibiAiReportService;

    public CIBIResponseDto cibiReport(
            String language,
            UUID userId,
            CIBIRequestDto body
    ){
        AnalyzedDto analyzed = this.analyzedDto(userId);

        Integer remainingBudged = this.remainingBudged(analyzed, body.getPrice());

        CIBI_AiReportResponseDto cibiAiReportResponseDto = cibiAiReportService.ask(
                language,
                analyzed.getProfile().getCurrency(),
                String.valueOf(analyzed.getProfile().getMonthSalary()),
                String.valueOf(analyzed.getReport_balance()),
                String.valueOf(analyzed.getTotalSpent()),
                String.valueOf(analyzed.getSpentPercentOfSalary()),
                String.valueOf(analyzed.getAverageDailySpending()),
                String.valueOf(analyzed.getDailyLimit()),
                String.valueOf(analyzed.getDaysUntilSalary()),
                analyzed.getExpensiveCategory() != null
                        ? analyzed.getExpensiveCategory().name()
                        : "NONE",
                analyzed.getFinanceResult(),
                body.getDescription(),
                String.valueOf(body.getPrice()),
                String.valueOf(remainingBudged)
        );

        return CIBIResponseDto.builder()
                .aiResponse(cibiAiReportResponseDto.getAiResponse())
                .remainingBudget(String.valueOf(remainingBudged))
                .build();
    }

    private Integer remainingBudged(AnalyzedDto analyzedDto, Integer price){
        return analyzedDto.getProfile().getBalance() - price;
    }

    private AnalyzedDto analyzedDto(UUID userId) {
        Optional<AnalyzedDto> analyzed = this.analyzeUserService.analyze(userId);

        if (analyzed.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "NOT_ENOUG_DATA");

        return analyzed.get();
    }
}
