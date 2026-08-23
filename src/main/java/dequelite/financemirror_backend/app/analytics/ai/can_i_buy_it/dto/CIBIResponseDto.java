package dequelite.financemirror_backend.app.analytics.ai.can_i_buy_it.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CIBIResponseDto {
    private String aiResponse;
    private String remainingBudget;
}
