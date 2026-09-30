package dequelite.financemirror_backend.domain.report;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReportRepository extends JpaRepository<ReportEntity, UUID> {
    Optional<ReportEntity>
    findTopByProfile_IdOrderByCreatedAtDesc(UUID profileId);

    @Query("""
        SELECT r
        FROM ReportEntity r
        WHERE r.profile.id = :profileId
          AND r.createdAt >= :start
          AND r.createdAt < :end
        ORDER BY r.createdAt DESC
        LIMIT 1
    """)
    Optional<ReportEntity> findLatestReportByPeriod(
            @Param("profileId") UUID profileId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
