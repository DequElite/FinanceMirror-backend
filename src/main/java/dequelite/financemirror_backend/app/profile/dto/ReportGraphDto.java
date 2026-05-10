package dequelite.financemirror_backend.app.profile.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportGraphDto {
    private UUID id;

    private Integer weeklySpending;

    private String expensiveCategory;

    private Integer remains;

    private String expenditureRate;

    private String leakProblem;

    private String advice;
}