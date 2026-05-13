package dequelite.financemirror_backend.app.profile.dto;

import dequelite.financemirror_backend.domain.dto.ReportDto;
import dequelite.financemirror_backend.domain.dto.SpendingDto;
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

    private List<SpendingDto> spendings;

    private List<ReportDto> reports;
}