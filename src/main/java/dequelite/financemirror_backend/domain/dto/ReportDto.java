package dequelite.financemirror_backend.domain.dto;

import dequelite.financemirror_backend.domain.report.ReportTypeEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
public class ReportDto {
    private UUID id;
    private ReportTypeEnum type;
    private Integer weeklySpending;
    private Integer spentPercentOfSalary;
    private Integer averageDailySpending;
    private Integer dailyLimit;
    private Integer reportBalance;
    private String expensiveCategory;
    private String financeResult;
    private String expenditureRate;
    private String leakProblem;
    private String advice;
    private LocalDateTime created_at;
}