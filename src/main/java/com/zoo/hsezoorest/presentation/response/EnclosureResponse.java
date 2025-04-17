package com.zoo.hsezoorest.presentation.response;

import com.zoo.hsezoorest.domain.model.enclosure.EnclosureType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnclosureResponse {
    private String id;
    private EnclosureType type;
    private int capacity;
    private int currentAnimalCount;
    private int remainingCapacity;
    private boolean isClean;
    private LocalDateTime lastCleaningTime;
    private List<AnimalInfo> animals;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnimalInfo {
        private String id;
        private String name;
        private String species;
        private HealthStatus status;

        public enum HealthStatus {
            HEALTHY, SICK, UNDER_OBSERVATION
        }
    }
}
