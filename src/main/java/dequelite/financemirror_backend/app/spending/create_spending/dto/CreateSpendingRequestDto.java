package dequelite.financemirror_backend.app.spending.create_spending.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class CreateSpendingRequestDto {

    @NotNull(message = "Date is required")
    @PastOrPresent(message = "Date cannot be in the future")
    private LocalDate date;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than 0")
    @Max(value = 10000000, message = "Price is too large")
    private Integer price;

    @NotBlank(message = "Category is required")
    @Size(
            min = 2,
            max = 50,
            message = "Category must contain between 2 and 50 characters"
    )
    private String category;

    @NotBlank(message = "Description is required")
    @Size(
            min = 2,
            max = 255,
            message = "Description must contain between 2 and 255 characters"
    )
    private String description;
}