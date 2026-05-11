package dequelite.financemirror_backend.domain.spending;

import com.fasterxml.jackson.annotation.JsonBackReference;
import dequelite.financemirror_backend.domain.profile.ProfileEntity;
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
@Table(name = "spending")
public class SpendingEntity {
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
    private Integer price;

    @Column(unique = false, nullable = false, updatable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    private CategoryEnum category;
}
