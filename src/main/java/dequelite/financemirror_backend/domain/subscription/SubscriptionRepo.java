package dequelite.financemirror_backend.domain.subscription;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubscriptionRepo extends JpaRepository<SubscriptionEntity, UUID> {
    Optional<SubscriptionEntity> findByUser_Id(UUID userId);
}
