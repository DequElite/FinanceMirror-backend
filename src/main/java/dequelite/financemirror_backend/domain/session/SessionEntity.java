package dequelite.financemirror_backend.domain.session;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
@Table(name = "session")
public class SessionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, unique = true, updatable = false)
    private UUID id;

    @OneToOne(mappedBy = "session", cascade = CascadeType.ALL)
    @JsonBackReference
    private UserEntity user;

    @Column(name = "refresh_token", nullable = false, unique = false, updatable = true)
    private String refreshToken;

    @Column(name = "refresh_token_expires_at", nullable = false, unique = false, updatable = true)
    private LocalDateTime refreshTokenExpiresAt;

    @Column(name = "access_token_id", nullable = true, unique = true, updatable = true)
    private String accessTokenId;

    @Column(name = "latest_login", nullable = false, unique = false, updatable = true)
    private LocalDateTime latestLogin;
}
