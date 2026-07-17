package dequelite.financemirror_backend.domain.biling;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BillingRepo extends JpaRepository<BillingEntity, UUID> {
    Optional<BillingEntity> findByPaypal_Capture_Id(String paypal_capture_id);
}
