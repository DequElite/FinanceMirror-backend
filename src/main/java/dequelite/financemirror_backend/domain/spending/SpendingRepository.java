package dequelite.financemirror_backend.domain.spending;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SpendingRepository extends JpaRepository<SpendingEntity, UUID> {
}
