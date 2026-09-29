package dequelite.financemirror_backend.app.analytics.dashboard;

import dequelite.financemirror_backend.app.analytics.dashboard.dto.DashboardGraphDto;
import dequelite.financemirror_backend.app.common.mapper.EntityMapper;
import dequelite.financemirror_backend.app.common.response.ApiException;
import dequelite.financemirror_backend.app.common.response.ErrorCode;
import dequelite.financemirror_backend.domain.dto.HistoryReportDto;
import dequelite.financemirror_backend.domain.dto.ReportDto;
import dequelite.financemirror_backend.domain.history_report.HistoryReportEntity;
import dequelite.financemirror_backend.domain.profile.ProfileEntity;
import dequelite.financemirror_backend.domain.profile.ProfileRepository;
import dequelite.financemirror_backend.domain.report.ReportEntity;
import dequelite.financemirror_backend.domain.report.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final ProfileRepository profileRepository;
    private final ReportRepository reportRepository;

    public DashboardGraphDto dashboard(UUID userId) {
        ProfileEntity profile = this.profileRepository
                .findByUser_Id(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        ReportDto latestReport = this.getLatestReport(profile);
        long leftUntilPayday = this.daysUntilEndOfMonth();
        List<ReportDto> reports = this.getReports(profile);
        List<HistoryReportDto> historyReports = this.getHistoryReports(profile);

        return DashboardGraphDto
                .builder()
                .latest_report(latestReport)
                .left_until_payday(leftUntilPayday)
                .reports(reports)
                .history_reports(historyReports)
                .build();
    }

    private List<ReportDto> getReports(
            ProfileEntity profile
    ) {
        List<ReportEntity> reportEntities = profile.getReports();

        return reportEntities.stream()
                .map(EntityMapper::toReportDto)
                .toList();
    }

    private List<HistoryReportDto> getHistoryReports(
            ProfileEntity profile
    ) {
        List<HistoryReportEntity> historyReportEntities = profile.getHistoryReports();

        return historyReportEntities.stream()
                .map(EntityMapper::toHistoryReportDto)
                .toList();
    }

    private ReportDto getLatestReport(
            ProfileEntity profile
    ) {
        Optional<ReportEntity> latestReport = this.reportRepository.findTopByProfile_IdOrderByCreatedAtDesc(profile.getId());

        return latestReport
                .map(EntityMapper::toReportDto)
                .orElse(null);
    }

    private long daysUntilEndOfMonth() {

        LocalDate now = LocalDate.now();

        LocalDate endOfMonth = now.withDayOfMonth(
                now.lengthOfMonth()
        );

        return ChronoUnit.DAYS.between(
                now,
                endOfMonth
        );
    }

}
