package dequelite.financemirror_backend.app.analytics.dashboard.dto;

import dequelite.financemirror_backend.domain.dto.HistoryReportDto;
import dequelite.financemirror_backend.domain.dto.ReportDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
public class DashboardGraphDto {
    private ReportDto latest_report;
    private Long left_until_payday;
    private List<ReportDto> reports;
    private List<HistoryReportDto> history_reports;
}
