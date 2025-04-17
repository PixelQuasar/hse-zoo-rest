package com.zoo.hsezoorest.presentation.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnimalTransferRequest {

    @NotBlank(message = "Animal ID is required")
    private String animalId;

    @NotBlank(message = "Target enclosure ID is required")
    private String enclosureId;

    private String reason;
}
