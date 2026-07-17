package dequelite.financemirror_backend.domain.verification;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import dequelite.financemirror_backend.domain.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "verification")
public class VerificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, unique = true, updatable = false)
    private UUID id;

    @Column(nullable = false, unique = true, updatable = false)
    private String token;

    @Column(nullable = false, unique = false, updatable = false)
    private String code;

    @Column(name = "expires_at", nullable = false, updatable = false, unique = false)
    private LocalDateTime expiresAt;

    @Column(nullable = true, unique = false, updatable = true)
    private Integer attempts = 0;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
