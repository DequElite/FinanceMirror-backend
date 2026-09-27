package dequelite.financemirror_backend.app.analytics.ai.ai_report;

import dequelite.financemirror_backend.app.analytics.ai.ai_report.dto.AiReportResponseDto;
import dequelite.financemirror_backend.app.analytics.ai.ai_report.dto.AnalyzedDto;
import dequelite.financemirror_backend.app.analytics.analysis.AnalyzeUserService;
import dequelite.financemirror_backend.domain.report.ReportEntity;
import dequelite.financemirror_backend.domain.report.ReportRepository;
import dequelite.financemirror_backend.domain.report.ReportTypeEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final AnalyzeUserService analyzeUserService;
    private final AiReportService aiReportService;

    private final ReportRepository reportRepository;

    public void createReport(UUID userId, String language){
        Optional<AnalyzedDto> analyzed = this.analyzeUserService.analyze(userId);

        if (analyzed.isEmpty()) return;

        AnalyzedDto analyzedDto = analyzed.get();

        AiReportResponseDto aiReportResponseDto = aiReportService.ask(
                language,
                analyzedDto.getProfile().getCurrency(),
                String.valueOf(analyzedDto.getProfile().getMonthSalary()),
                String.valueOf(analyzedDto.getReport_balance()),
                String.valueOf(analyzedDto.getTotalSpent()),
                String.valueOf(analyzedDto.getSpentPercentOfSalary()),
                String.valueOf(analyzedDto.getAverageDailySpending()),
                String.valueOf(analyzedDto.getDailyLimit()),
                String.valueOf(analyzedDto.getDaysUntilSalary()),
                analyzedDto.getExpensiveCategory() != null
                        ? analyzedDto.getExpensiveCategory().name()
                        : "NONE",
                analyzedDto.getFinanceResult()
        );

        ReportEntity newReport = ReportEntity.builder()
                .profile(analyzedDto.getProfile())
                .type(ReportTypeEnum.AI)
                .totalSpent(analyzedDto.getTotalSpent())
                .expensive_category(analyzedDto.getExpensiveCategory())
                .spentPercentOfSalary(analyzedDto.getSpentPercentOfSalary())
                .averageDailySpending(analyzedDto.getAverageDailySpending())
                .dailyLimit(analyzedDto.getDailyLimit())
                .reportBalance(analyzedDto.getReport_balance())
                .expenditureRate(aiReportResponseDto.getExpenditure_rate())
                .leakProblem(aiReportResponseDto.getLeak_problem())
                .advice(aiReportResponseDto.getAdvice())
                .financeResult(analyzedDto.getFinanceResult())
                .build();

        this.reportRepository.save(newReport);
    }
}
