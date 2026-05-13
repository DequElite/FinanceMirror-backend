package dequelite.financemirror_backend.app.analytics.dashboard.dto;

import dequelite.financemirror_backend.domain.dto.ReportDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DashboardGraphDto {
    private ReportDto latest_report;
    private Long left_until_payday;
}
