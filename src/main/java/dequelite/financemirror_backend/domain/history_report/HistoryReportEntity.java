package dequelite.financemirror_backend.domain.history_report;

import com.fasterxml.jackson.annotation.JsonBackReference;
import dequelite.financemirror_backend.domain.profile.ProfileEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(
        name = "history_report",
        uniqueConstraints = @UniqueConstraint(
                name = "uc_history_report_profile_period_start",
                columnNames = {"profile_id", "period_start_date"}
        )
)
public class HistoryReportEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, unique = true, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    @JsonBackReference
    private ProfileEntity profile;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDate createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDate updatedAt;

    @Column(name = "period_start_date", nullable = false)
    private LocalDate periodStartDate;

    @Column(name = "period_end_date", nullable = false)
    private LocalDate periodEndDate;

    @Column(name = "total_spent", nullable = false)
    private Integer totalSpent;

    @Column(name = "current_balance", nullable = false)
    private Integer currentBalance;

    @Column(name = "total_replenishments", nullable = false)
    private Integer totalReplenishments;

    @Column(name = "average_daily_spending", nullable = false)
    private Integer averageDailySpending;

    @Column(name = "spent_difference")
    private Integer spentDifference;

    @Column(name = "spent_difference_percent")
    private Integer spentDifferencePercent;

    @Column(name = "replenished_difference")
    private Integer replenishedDifference;

    @Column(name = "replenished_difference_percent")
    private Integer replenishedDifferencePercent;

    @Column(name = "balance_difference")
    private Integer balanceDifference;

    @Column(name = "balance_difference_percent")
    private Integer balanceDifferencePercent;

    @Column(name = "average_daily_spending_difference")
    private Integer averageDailySpendingDifference;

    @Column(name = "average_daily_spending_difference_percent")
    private Integer averageDailySpendingDifferencePercent;
}
