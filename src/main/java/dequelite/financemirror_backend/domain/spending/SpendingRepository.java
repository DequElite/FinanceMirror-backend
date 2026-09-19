package dequelite.financemirror_backend.domain.spending;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpendingRepository extends JpaRepository<SpendingEntity, UUID> {
    List<SpendingEntity> findAllByProfile_IdAndDateBetween(
            UUID profileId,
            LocalDate startDate,
            LocalDate endDate
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<SpendingEntity> findByIdAndProfile_Id(
            UUID spendingId,
            UUID profileId
    );
}
