package dequelite.financemirror_backend.domain.spending;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface SpendingRepository extends JpaRepository<SpendingEntity, UUID> {
    List<SpendingEntity> findAllByProfile_IdAndDateBetween(
            UUID profileId,
            LocalDate startDate,
            LocalDate endDate
    );
}
