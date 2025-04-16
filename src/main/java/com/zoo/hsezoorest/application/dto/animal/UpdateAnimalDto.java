package com.zoo.hsezoorest.application.dto.animal;

import com.zoo.hsezoorest.domain.model.animal.Gender;
import com.zoo.hsezoorest.domain.model.animal.HealthStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAnimalDto {
    private String name;
    private String species;
    private Boolean isPredator;
    private LocalDate birthDate;
    private Gender gender;
    private String favoriteFood;
    private HealthStatus healthStatus;
    private String enclosureId;
}
