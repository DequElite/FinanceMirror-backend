package dequelite.financemirror_backend.app.analytics.history_report;

import dequelite.financemirror_backend.app.common.response.ApiException;
import dequelite.financemirror_backend.app.common.response.ErrorCode;
import dequelite.financemirror_backend.domain.history_report.HistoryReportEntity;
import dequelite.financemirror_backend.domain.history_report.HistoryReportRepo;
import dequelite.financemirror_backend.domain.profile.ProfileEntity;
import dequelite.financemirror_backend.domain.profile.ProfileRepository;
import dequelite.financemirror_backend.domain.replenishment.ReplenishmentEntity;
import dequelite.financemirror_backend.domain.replenishment.ReplenishmentRepo;
import dequelite.financemirror_backend.domain.report.ReportEntity;
import dequelite.financemirror_backend.domain.report.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HistoryReportService {
    private final ProfileRepository profileRepository;
    private final HistoryReportRepo historyReportRepo;
    private final ReportRepository reportRepository;
    private final ReplenishmentRepo replenishmentRepo;

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void createHistoryReport(UUID userId) {
        ProfileEntity profile = this.profileRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        LocalDate periodStartDate = LocalDate.now().withDayOfMonth(1);
        LocalDate periodEndDate = LocalDate.now();

        LocalDate previousPeriodStartDate = periodStartDate.minusMonths(1);
        LocalDate previousPeriodEndDate = periodStartDate.minusDays(1);

        HistoryReportEntity currentHistoryReport = this.currentHistoryReport(
                profile,
                periodStartDate,
                periodEndDate
        );

        HistoryReportEntity previousHistoryReport = this.previousHistoryReport(
                profile,
                previousPeriodStartDate,
                previousPeriodEndDate
        );

        this.calculateDifferences(
                currentHistoryReport,
                previousHistoryReport
        );

        this.historyReportRepo.save(currentHistoryReport);
    }

    private void calculateDifferences(
            HistoryReportEntity currentReport,
            HistoryReportEntity previousReport
    ) {
        // Total spent
        int spentDifference =
                currentReport.getTotalSpent() - previousReport.getTotalSpent();

        Integer spentDifferencePercent = calculatePercentDifference(
                spentDifference,
                previousReport.getTotalSpent()
        );

        // Total replenishments
        int replenishedDifference =
                currentReport.getTotalReplenishments()
                        - previousReport.getTotalReplenishments();

        Integer replenishedDifferencePercent = calculatePercentDifference(
                replenishedDifference,
                previousReport.getTotalReplenishments()
        );

        // Balance
        int balanceDifference =
                currentReport.getCurrentBalance()
                        - previousReport.getCurrentBalance();

        Integer balanceDifferencePercent = calculatePercentDifference(
                balanceDifference,
                previousReport.getCurrentBalance()
        );

        // Average daily spending
        int averageDailySpendingDifference =
                currentReport.getAverageDailySpending()
                        - previousReport.getAverageDailySpending();

        Integer averageDailySpendingDifferencePercent = calculatePercentDifference(
                averageDailySpendingDifference,
                previousReport.getAverageDailySpending()
        );

        // Set differences
        currentReport.setSpentDifference(spentDifference);
        currentReport.setSpentDifferencePercent(spentDifferencePercent);

        currentReport.setReplenishedDifference(replenishedDifference);
        currentReport.setReplenishedDifferencePercent(replenishedDifferencePercent);

        currentReport.setBalanceDifference(balanceDifference);
        currentReport.setBalanceDifferencePercent(balanceDifferencePercent);

        currentReport.setAverageDailySpendingDifference(
                averageDailySpendingDifference
        );
        currentReport.setAverageDailySpendingDifferencePercent(
                averageDailySpendingDifferencePercent
        );
    }

    private Integer calculatePercentDifference(
            int difference,
            int previousValue
    ) {
        if (previousValue == 0) {
            return null;
        }

        return (int) (((double) difference / previousValue) * 100);
    }

    private HistoryReportEntity previousHistoryReport(
            ProfileEntity profile,
            LocalDate periodStartDate,
            LocalDate periodEndDate
    ) {
        Optional<HistoryReportEntity> foundPreviousHistoryReport =
                this.historyReportRepo.findByProfileIdAndPeriodStartDate(
                        profile.getId(),
                        periodStartDate
                );

        HistoryReportEntity previousHistoryReport;

        if (foundPreviousHistoryReport.isPresent()) {
            previousHistoryReport = foundPreviousHistoryReport.get();
        } else {
            previousHistoryReport = this.calculateReport(
                    profile,
                    periodStartDate,
                    periodEndDate
            );

            this.historyReportRepo.save(previousHistoryReport);
        }

        return previousHistoryReport;
    }

    private HistoryReportEntity currentHistoryReport(
            ProfileEntity profile,
            LocalDate periodStartDate,
            LocalDate periodEndDate
    ){
        Optional<HistoryReportEntity> foundHistoryReport = this.historyReportRepo.findByProfileIdAndPeriodStartDate(
                profile.getId(),
                periodStartDate
        );

        HistoryReportEntity currentCalculatedReport = this.calculateReport(
                profile,
                periodStartDate,
                periodEndDate
        );

        foundHistoryReport.ifPresent(historyReportEntity -> currentCalculatedReport.setId(historyReportEntity.getId()));

        return currentCalculatedReport;
    }

    private HistoryReportEntity calculateReport(
            ProfileEntity profile,
            LocalDate reportStartDate,
            LocalDate reportEndDate
    ) {
        List<ReplenishmentEntity> replenishments = this.replenishmentRepo.findAllByProfile_IdAndDateBetween(
                profile.getId(),
                reportStartDate,
                reportEndDate
        );

        ReportEntity latestReport = this.reportRepository.findLatestReportByPeriod(
                        profile.getId(),
                        reportStartDate,
                        reportEndDate
                )
                .orElseThrow(() -> new ApiException(ErrorCode.INVALID_CREDENTIALS));

        Integer totalSpent = latestReport.getTotalSpent();
        Integer averageDailySpending = latestReport.getAverageDailySpending();
        Integer balance = latestReport.getReportBalance();
        Integer totalReplenishments = replenishments.stream()
                .mapToInt(ReplenishmentEntity::getAmount)
                .sum();

        return HistoryReportEntity.builder()
                .profile(profile)
                .periodStartDate(reportStartDate)
                .periodEndDate(reportEndDate)
                .totalSpent(totalSpent)
                .currentBalance(balance)
                .totalReplenishments(totalReplenishments)
                .averageDailySpending(averageDailySpending)
                .build();
    }
}
