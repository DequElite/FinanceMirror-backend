package dequelite.financemirror_backend.app.profile.balance.update_balance.dto;

import dequelite.financemirror_backend.domain.replenishment.ReplenishmentEnum;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class UpdateBalanceRequestDto {

    @NotNull
    @Min(0)
    private Integer amount;

    @NotNull
    private ReplenishmentEnum type;

    @NotNull
    private LocalDate date;

    private String description;
}