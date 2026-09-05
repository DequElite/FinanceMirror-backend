package dequelite.financemirror_backend.domain.session;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
@Table(name = "session")
public class SessionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, unique = true, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private UserEntity user;

    @Column(name = "refresh_token", nullable = true, unique = false, updatable = true)
    private String refreshToken;

    @Column(name = "access_token_id", nullable = true, unique = true, updatable = true)
    private String accessTokenId;

    @Column(name = "expires_at", nullable = false, unique = false, updatable = false)
    private LocalDateTime expiresAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "ip_address", unique = false, nullable = false, updatable = false)
    private String ipAddress;

    @Column(name = "user_agent", unique = false, nullable = false, updatable = false)
    private String userAgent;

}
