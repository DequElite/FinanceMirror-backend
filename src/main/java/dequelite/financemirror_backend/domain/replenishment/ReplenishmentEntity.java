package dequelite.financemirror_backend.domain.replenishment;

import com.fasterxml.jackson.annotation.JsonBackReference;
import dequelite.financemirror_backend.domain.profile.ProfileEntity;
import dequelite.financemirror_backend.domain.spending.CategoryEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "replenishment")
public class ReplenishmentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, unique = true, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    @JsonBackReference
    private ProfileEntity profile;

    @Column(unique = false, nullable = false, updatable = false)
    private LocalDate date;

    @Column(unique = false, nullable = false, updatable = false)
    private Integer amount;

    @Column(unique = false, nullable = false, updatable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    private ReplenishmentEnum type;
}
