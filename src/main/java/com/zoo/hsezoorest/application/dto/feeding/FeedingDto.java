package com.zoo.hsezoorest.application.dto.feeding;

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
public class FeedingDto {
    private String id;
    private String animalId;
    private String animalName;
    private String animalSpecies;
    private LocalTime feedingTime;
    private FoodType foodType;
    private boolean isCompleted;
    private LocalDateTime lastFedTime;
    private List<String> feedingHistory;
}
