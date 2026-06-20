package dequelite.financemirror_backend.domain.replenishment;

import dequelite.financemirror_backend.domain.spending.SpendingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReplenishmentRepo extends JpaRepository<ReplenishmentEntity, UUID> {
    List<ReplenishmentEntity> findAllByProfile_IdAndDateBetween(
            UUID profileId,
            LocalDate startDate,
            LocalDate endDate
    );
}
