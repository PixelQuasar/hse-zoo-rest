package com.zoo.hsezoorest.application.dto.feeding;

import com.zoo.hsezoorest.domain.model.feeding.FoodType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateFeedingDto {
    @NotBlank(message = "Animal ID is required")
    private String animalId;

    @NotNull(message = "Feeding time is required")
    private LocalTime feedingTime;

    @NotNull(message = "Food type is required")
    private FoodType foodType;
}
