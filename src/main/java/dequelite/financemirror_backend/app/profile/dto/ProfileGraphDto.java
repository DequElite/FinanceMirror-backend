package dequelite.financemirror_backend.app.profile.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileGraphDto {
    private UUID id;

    private String email;

    private Integer monthSalary;

    private List<SpendingGraphDto> spendings;

    private List<ReportGraphDto> reports;
}