package dequelite.financemirror_backend.domain.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import dequelite.financemirror_backend.domain.profile.ProfileEntity;
import dequelite.financemirror_backend.domain.session.SessionEntity;
import dequelite.financemirror_backend.domain.subscription.SubscriptionEntity;
import dequelite.financemirror_backend.domain.verification.VerificationEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "app_user")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, unique = true, updatable = false)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = false)
    private String password;

    @Column(nullable = false, unique = false, updatable = true)
    private Boolean verified = false;

    @Column(name = "created_at", nullable = false, updatable = false, unique = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    @JsonManagedReference
    private ProfileEntity profile;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<SessionEntity> sessions;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id")
    @JsonManagedReference
    private SubscriptionEntity subscription;
}
