package dequelite.financemirror_backend.app.analytics.report;

import dequelite.financemirror_backend.app.analytics.report.dto.AiReportResponseDto;
import dequelite.financemirror_backend.app.analytics.report.dto.AnalyzedDto;
import dequelite.financemirror_backend.domain.report.ReportEntity;
import dequelite.financemirror_backend.domain.report.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final AnalyzeUserService analyzeUserService;
    private final AiReportService aiReportService;

    private final ReportRepository reportRepository;

    public void createReport(UUID userId){
        AnalyzedDto analyzedDto = this.analyzeUserService.analyze(userId);

        AiReportResponseDto aiReportResponseDto = aiReportService.ask(
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
                        : "NONE"
        );

        ReportEntity newReport = ReportEntity.builder()
                .profile(analyzedDto.getProfile())
                .weeklySpending(analyzedDto.getTotalSpent())
                .expensive_category(analyzedDto.getExpensiveCategory())
                .spentPercentOfSalary(analyzedDto.getSpentPercentOfSalary())
                .averageDailySpending(analyzedDto.getAverageDailySpending())
                .dailyLimit(analyzedDto.getDailyLimit())
                .reportBalance(analyzedDto.getReport_balance())
                .expenditureRate(aiReportResponseDto.getExpenditure_rate())
                .leakProblem(aiReportResponseDto.getLeak_problem())
                .advice(aiReportResponseDto.getAdvice())
                .build();

        this.reportRepository.save(newReport);
    }
}
