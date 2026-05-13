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

        String weeklySpending = String.valueOf(analyzedDto.getTotalSpent());
        String expensive_category = analyzedDto.getExpensiveCategory().toString();
        String remains = String.valueOf(analyzedDto.getRemains());

        AiReportResponseDto aiReportResponseDto = this.aiReportService.ask(
            String.valueOf(analyzedDto.getProfile().getMonthSalary()),
            weeklySpending,
            remains,
            String.valueOf(analyzedDto.getDaysUntilSalary()),
            expensive_category
        );

        ReportEntity newReport = ReportEntity.builder()
                .profile(analyzedDto.getProfile())
                .weeklySpending(analyzedDto.getTotalSpent())
                .expensive_category(analyzedDto.getExpensiveCategory())
                .remains(analyzedDto.getRemains())
                .expenditureRate(aiReportResponseDto.getExpenditure_rate())
                .leakProblem(aiReportResponseDto.getLeak_problem())
                .advice(aiReportResponseDto.getAdvice())
                .build();

        this.reportRepository.save(newReport);
    }
}
