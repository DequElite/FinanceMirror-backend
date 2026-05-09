package dequelite.financemirror_backend.domain.profile;

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
    private UserEntity user;

    @Column(name = "month_salary", unique = false, nullable = false, updatable = true)
    private Integer monthSalary;

    @OneToMany(mappedBy = "profile")
    private List<SpendingEntity> spendings;

    @OneToMany(mappedBy = "profile")
    private List<ReportEntity> reports;
}
