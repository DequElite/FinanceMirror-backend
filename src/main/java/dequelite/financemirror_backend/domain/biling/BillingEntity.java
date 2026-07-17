package dequelite.financemirror_backend.domain.biling;

import com.fasterxml.jackson.databind.JsonNode;
import dequelite.financemirror_backend.domain.subscription.SubscriptionEntity;
import dequelite.financemirror_backend.domain.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "subscription")
public class BillingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, unique = true, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private UserEntity user;

    @OneToOne
    @JoinColumn(name = "subscription_id", nullable = false, unique = false, updatable = false)
    private SubscriptionEntity subscription;

    @Column(name = "paypal_order_id", nullable = false, unique = false, updatable = false)
    private String paypalOrderId;

    @Column(name = "paypal_capture_id", nullable = false, unique = false, updatable = false)
    private String paypalCaptureId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "paypal_response", nullable = false, unique = false, columnDefinition = "jsonb", updatable = false)
    private JsonNode paypalResponse;

    @Column(nullable = false, unique = false, updatable = false)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency = "UAH";

    @Enumerated(EnumType.STRING)
    private BillingStatus status;

    @Column(unique = false, nullable = true, updatable = false)
    @CreationTimestamp
    private LocalDate created_at;
}
