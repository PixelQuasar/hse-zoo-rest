package com.zoo.hsezoorest.application.dto.enclosure;

import com.zoo.hsezoorest.domain.model.enclosure.EnclosureType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateEnclosureDto {
    @NotNull(message = "Enclosure type is required")
    private EnclosureType type;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;
}
