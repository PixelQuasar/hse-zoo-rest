package com.zoo.hsezoorest.application.dto.enclosure;

import com.zoo.hsezoorest.application.dto.animal.AnimalDto;
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
public class EnclosureDto {
    private String id;
    private EnclosureType type;
    private int capacity;
    private int currentAnimalCount;
    private int remainingCapacity;
    private boolean isClean;
    private LocalDateTime lastCleaningTime;
    private List<AnimalDto> animals;
}
