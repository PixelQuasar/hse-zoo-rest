package com.zoo.hsezoorest.presentation.request;

import com.zoo.hsezoorest.domain.model.enclosure.EnclosureType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnclosureRequest {

    @NotNull(message = "Enclosure type is required")
    private EnclosureType type;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;
}
