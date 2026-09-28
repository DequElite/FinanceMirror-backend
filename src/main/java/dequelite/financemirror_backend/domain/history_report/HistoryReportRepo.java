package dequelite.financemirror_backend.domain.history_report;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HistoryReportRepo extends JpaRepository<HistoryReportEntity, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT h
        FROM HistoryReport h
        WHERE h.profile.id = :profileId
          AND h.periodStartDate = :periodStart
    """)
    Optional<HistoryReportEntity> findByProfileIdAndPeriodStartDate(UUID profileId, LocalDate periodStartDate);
}
