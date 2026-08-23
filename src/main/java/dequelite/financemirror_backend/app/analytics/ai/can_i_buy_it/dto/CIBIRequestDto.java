package dequelite.financemirror_backend.app.analytics.ai.can_i_buy_it.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;

@Getter
public class CIBIRequestDto {
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than 0")
    @Max(value = 10000000, message = "Price is too large")
    private Integer price;

    @NotBlank(message = "Description is required")
    @Size(
            min = 2,
            max = 255,
            message = "Description must contain between 2 and 255 characters"
    )
    private String description;
}
