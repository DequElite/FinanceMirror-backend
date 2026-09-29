package dequelite.financemirror_backend.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
public class HistoryReportDto {
    private UUID id;

    private LocalDate createdAt;
    private LocalDate updatedAt;

    private LocalDate periodStartDate;
    private LocalDate periodEndDate;

    private Integer totalSpent;
    private Integer currentBalance;
    private Integer totalReplenishments;
    private Integer averageDailySpending;

    private Integer spentDifference;
    private Integer spentDifferencePercent;

    private Integer replenishedDifference;
    private Integer replenishedDifferencePercent;

    private Integer balanceDifference;
    private Integer balanceDifferencePercent;

    private Integer averageDailySpendingDifference;
    private Integer averageDailySpendingDifferencePercent;
}