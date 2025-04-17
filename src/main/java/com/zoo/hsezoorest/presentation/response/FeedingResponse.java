package com.zoo.hsezoorest.presentation.response;

import com.zoo.hsezoorest.domain.model.feeding.FoodType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedingResponse {
    private String id;
    private AnimalInfo animal;
    private LocalTime feedingTime;
    private FoodType foodType;
    private boolean isCompleted;
    private LocalDateTime lastFedTime;
    private List<String> feedingHistory; // Dates as strings in format YYYY-MM-DD

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnimalInfo {
        private String id;
        private String name;
        private String species;
        private String enclosureId;
    }
}
