package dequelite.financemirror_backend.domain.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import dequelite.financemirror_backend.domain.profile.ProfileEntity;
import dequelite.financemirror_backend.domain.replenishment.ReplenishmentEnum;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
public class ReplenishmentDto {
    private UUID id;
    private LocalDate date;
    private Integer amount;
    private String description;
    private ReplenishmentEnum category;
}
