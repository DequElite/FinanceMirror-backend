package dequelite.financemirror_backend.app.analytics.ai.ai_report.dto;

import dequelite.financemirror_backend.domain.profile.ProfileEntity;
import dequelite.financemirror_backend.domain.spending.CategoryEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AnalyzedDto {
    private int totalSpent;
    private CategoryEnum expensiveCategory;
    private int spentPercentOfSalary;
    private int averageDailySpending;
    private int dailyLimit;
    private int report_balance;
    private ProfileEntity profile;
    private long daysUntilSalary;
    private String financeResult;
}
