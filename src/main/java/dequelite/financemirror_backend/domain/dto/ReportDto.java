package dequelite.financemirror_backend.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class ReportDto {
    private UUID id;
    private Integer weeklySpending;
    private Integer remains;
    private String expenditureRate;
    private String leakProblem;
    private String advice;
}