package dequelite.financemirror_backend.app.analytics.ai_report.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AiReportResponseDto {
    private String expenditure_rate;
    private String leak_problem;
    private String advice;
}
