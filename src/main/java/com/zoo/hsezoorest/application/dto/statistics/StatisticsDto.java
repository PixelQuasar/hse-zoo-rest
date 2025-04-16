package com.zoo.hsezoorest.application.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsDto {
    // General statistics
    private long totalAnimals;
    private long totalEnclosures;
    private long emptyEnclosures;
    private long totalFeedingSchedules;

    // Animal statistics
    private long healthyAnimals;
    private long sickAnimals;
    private long predators;
    private long herbivores;

    // Species distribution
    private Map<String, Long> animalsBySpecies;

    // Enclosure statistics
    private Map<String, Long> enclosuresByType;
    private int totalCapacity;
    private int usedCapacity;
    private double occupancyRate;

    // Feeding statistics
    private long pendingFeedings;
    private long completedFeedings;
    private Map<String, Long> feedingsByFoodType;
}
