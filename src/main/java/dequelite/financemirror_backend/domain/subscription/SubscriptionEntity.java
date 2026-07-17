package dequelite.financemirror_backend.domain.subscription;

import com.fasterxml.jackson.annotation.JsonBackReference;
import dequelite.financemirror_backend.domain.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "subscription")
public class SubscriptionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, unique = true, updatable = false)
    private UUID id;

    @OneToOne(mappedBy = "subscription", cascade = CascadeType.ALL)
    @JsonBackReference
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    private SubscriptionType type;

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;

    @Column(unique = false, nullable = true, updatable = false)
    @CreationTimestamp
    private LocalDate created_at;

    @Column(unique = false, nullable = false, updatable = true)
    private LocalDate started_at;

    @Column(unique = false, nullable = false, updatable = true)
    private LocalDate updated_at;

    @Column(unique = false, nullable = false, updatable = true)
    private LocalDate expires_at;
}
