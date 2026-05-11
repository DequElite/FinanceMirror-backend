package dequelite.financemirror_backend.domain.dto;

import dequelite.financemirror_backend.domain.spending.CategoryEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
public class SpendingDto {
    private UUID id;
    private Integer price;
    private String description;
    private CategoryEnum category;
    private LocalDate date;
}