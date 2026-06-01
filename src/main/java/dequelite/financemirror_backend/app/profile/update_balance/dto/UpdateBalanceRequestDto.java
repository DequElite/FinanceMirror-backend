package dequelite.financemirror_backend.app.profile.update_balance.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdateBalanceRequestDto {
    @NotNull
    @Min(0)
    private Integer amount;
}
