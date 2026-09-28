package dequelite.financemirror_backend.domain.report;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReportRepository extends JpaRepository<ReportEntity, UUID> {
    Optional<ReportEntity>
    findTopByProfile_IdOrderByCreatedAtDesc(UUID profileId);

    @Query("""
        SELECT r
        FROM report r
        WHERE r.profile_id = :profileId
          AND r.created_at BETWEEN :start AND :end
        ORDER BY r.created_at DESC
        LIMIT 1
    """)
    Optional<ReportEntity> findLatestReportByPeriod(
            UUID profileId,
            LocalDate start,
            LocalDate end
    );
}
