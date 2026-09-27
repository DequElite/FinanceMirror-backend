package dequelite.financemirror_backend.domain.history_report;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface HistoryReportRepo extends JpaRepository<UUID, HistoryReportEntity> {
}
