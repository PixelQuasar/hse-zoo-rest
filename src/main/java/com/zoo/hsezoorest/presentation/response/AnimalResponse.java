package com.zoo.hsezoorest.presentation.response;

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
public class AnimalResponse {
    private String id;
    private String name;
    private String species;
    private boolean isPredator;
    private LocalDate birthDate;
    private Gender gender;
    private String favoriteFood;
    private HealthStatus healthStatus;
    private EnclosureInfo currentEnclosure;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EnclosureInfo {
        private String id;
        private String type;
    }
}
