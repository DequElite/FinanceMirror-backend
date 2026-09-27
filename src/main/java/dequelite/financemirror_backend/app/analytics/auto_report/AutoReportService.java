package dequelite.financemirror_backend.app.analytics.auto_report;

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
public class AutoReportService {
    private final AnalyzeUserService analyzeUserService;

    private final ReportRepository reportRepository;

    public void createAutoReport(UUID userId) {
        Optional<AnalyzedDto> analyzed = this.analyzeUserService.analyze(userId);

        if (analyzed.isEmpty()) return;

        AnalyzedDto analyzedDto = analyzed.get();

        ReportEntity newReport = ReportEntity.builder()
                .profile(analyzedDto.getProfile())
                .type(ReportTypeEnum.AUTO)
                .totalSpent(analyzedDto.getTotalSpent())
                .expensive_category(analyzedDto.getExpensiveCategory())
                .spentPercentOfSalary(analyzedDto.getSpentPercentOfSalary())
                .averageDailySpending(analyzedDto.getAverageDailySpending())
                .dailyLimit(analyzedDto.getDailyLimit())
                .reportBalance(analyzedDto.getReport_balance())
                .financeResult(analyzedDto.getFinanceResult())
                .build();

        this.reportRepository.save(newReport);
    }
}
