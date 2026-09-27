package dequelite.financemirror_backend.domain.profile;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import dequelite.financemirror_backend.domain.history_report.HistoryReportEntity;
import dequelite.financemirror_backend.domain.replenishment.ReplenishmentEntity;
import dequelite.financemirror_backend.domain.report.ReportEntity;
import dequelite.financemirror_backend.domain.spending.SpendingEntity;
import dequelite.financemirror_backend.domain.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "profile")
public class ProfileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, unique = true, updatable = false)
    private UUID id;

    @OneToOne(mappedBy = "profile")
    @JsonBackReference
    private UserEntity user;

    @Column(name = "balance", unique = false, nullable = false, updatable = true)
    private Integer balance = 0;

    @Column(name = "month_salary", unique = false, nullable = false, updatable = true)
    private Integer monthSalary;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency = "UAH";

    @OneToMany(mappedBy = "profile")
    @JsonManagedReference
    private List<SpendingEntity> spendings;

    @OneToMany(mappedBy = "profile")
    @JsonManagedReference
    private List<ReplenishmentEntity> replenishments;

    @OneToMany(mappedBy = "profile")
    @JsonManagedReference
    private List<ReportEntity> reports;

    @OneToMany(mappedBy = "profile")
    @JsonManagedReference
    private List<HistoryReportEntity> historyReports;
}
