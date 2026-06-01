package dequelite.financemirror_backend.domain.report;

import com.fasterxml.jackson.annotation.JsonBackReference;
import dequelite.financemirror_backend.domain.profile.ProfileEntity;
import dequelite.financemirror_backend.domain.spending.CategoryEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "report")
public class ReportEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, unique = true, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    @JsonBackReference
    private ProfileEntity profile;

    @Column(name = "weekly_spending", unique = false, nullable = false, updatable = false)
    private Integer weeklySpending;

    @Enumerated(EnumType.STRING)
    @Column(name = "expensive_category", unique = false, nullable = false, updatable = false)
    private CategoryEnum expensive_category;

    @Column(name = "spent_percent_of_salary", nullable = false)
    private Integer spentPercentOfSalary;

    @Column(name = "average_daily_spending", nullable = false)
    private Integer averageDailySpending;

    @Column(name = "daily_limit", nullable = false)
    private Integer dailyLimit;

    @Column(name = "report_balance", unique = false, nullable = false, updatable = false)
    private Integer reportBalance;

    @Column(name = "expenditure_rate", unique = false, nullable = false, updatable = false)
    private String expenditureRate;

    @Column(name = "leak_problem", unique = false, nullable = false, updatable = false)
    private String leakProblem;

    @Column(name = "advice", unique = false, nullable = false, updatable = false)
    private String advice;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
