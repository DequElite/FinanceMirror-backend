package dequelite.financemirror_backend.domain.password_recovery;

import dequelite.financemirror_backend.domain.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "password_recovery")
public class PasswordRecoveryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, unique = true, updatable = false)
    private UUID id;

    @Column(nullable = false, unique = true, updatable = false)
    private String token;

    @Column(name = "expires_at", nullable = false, updatable = false, unique = false)
    private LocalDateTime expiresAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
