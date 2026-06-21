package dequelite.financemirror_backend.domain.dto;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
public class ReportDto {
    private UUID id;
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
}