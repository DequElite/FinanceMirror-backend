package dequelite.financemirror_backend.app.analytics.dashboard;

import dequelite.financemirror_backend.app.analytics.dashboard.dto.DashboardGraphDto;
import dequelite.financemirror_backend.app.common.mapper.EntityMapper;
import dequelite.financemirror_backend.domain.dto.ReportDto;
import dequelite.financemirror_backend.domain.profile.ProfileEntity;
import dequelite.financemirror_backend.domain.profile.ProfileRepository;
import dequelite.financemirror_backend.domain.report.ReportEntity;
import dequelite.financemirror_backend.domain.report.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final ProfileRepository profileRepository;
    private final ReportRepository reportRepository;

    public DashboardGraphDto dashboard(UUID userId) {
        Optional<ProfileEntity> foundProfile = this.profileRepository.findByUser_Id(userId);
        if(foundProfile.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        Optional<ReportEntity> latestReport = this.reportRepository.findTopByProfile_IdOrderByCreatedAtDesc(foundProfile.get().getId());
        if(latestReport.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        ReportDto reportDto = EntityMapper.toReportDto(latestReport.get());

        long leftUntilPayday = this.daysUntilEndOfMonth();

        return DashboardGraphDto
                .builder()
                .latest_report(reportDto)
                .left_until_payday(leftUntilPayday)
                .build();
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
