package dequelite.financemirror_backend.app.profile.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SpendingGraphDto {
    private UUID id;

    private LocalDate date;

    private Integer price;

    private String category;

    private String description;
}