package dequelite.financemirror_backend.app.profile.user_profile.dto;

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
@ToString
public class ProfileGraphDto {
    private UUID id;

    private String email;

    private Integer balance;
    private Integer monthSalary;
    private String currency;

    private List<SpendingDto> spendings;

    private List<ReportDto> reports;
}