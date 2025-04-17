package com.zoo.hsezoorest.presentation.controller;

import com.zoo.hsezoorest.application.dto.statistics.StatisticsDto;
import com.zoo.hsezoorest.application.service.ZooStatisticsService;
import com.zoo.hsezoorest.presentation.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@Tag(name = "Zoo Statistics", description = "APIs for retrieving zoo statistics")
public class StatisticsController {

    private final ZooStatisticsService statisticsService;

    @GetMapping
    @Operation(summary = "Get comprehensive zoo statistics",
            description = "Retrieves detailed statistics about the zoo")
    public ApiResponse<StatisticsDto> getZooStatistics() {
        log.info("Getting comprehensive zoo statistics");
        return ApiResponse.success(statisticsService.getZooStatistics());
    }

    @GetMapping("/summary")
    @Operation(summary = "Get summary statistics",
            description = "Retrieves a summary of important zoo metrics")
    public ApiResponse<Map<String, Object>> getStatisticsSummary() {
        log.info("Getting summary statistics");

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalAnimals", statisticsService.getTotalAnimalCount());
        summary.put("totalEnclosures", statisticsService.getTotalEnclosureCount());
        summary.put("emptyEnclosures", statisticsService.getEmptyEnclosureCount());
        summary.put("occupancyRate", String.format("%.2f%%", statisticsService.getOccupancyRate()));
        summary.put("sickAnimals", statisticsService.getSickAnimalCount());
        summary.put("pendingFeedings", statisticsService.getPendingFeedingCount());

        return ApiResponse.success(summary);
    }

    @GetMapping("/health-alerts")
    @Operation(summary = "Get health alerts",
            description = "Retrieves alerts about animals requiring attention")
    public ApiResponse<Map<String, Long>> getHealthAlerts() {
        log.info("Getting health alerts");

        Map<String, Long> alerts = new HashMap<>();
        long sickAnimals = statisticsService.getSickAnimalCount();
        alerts.put("sickAnimals", sickAnimals);
        alerts.put("pendingFeedings", statisticsService.getPendingFeedingCount());

        String message = sickAnimals > 0 ?
                "There are " + sickAnimals + " animals requiring medical attention" :
                "All animals are healthy";

        return ApiResponse.success(message, alerts);
    }

    @GetMapping("/capacity")
    @Operation(summary = "Get capacity statistics",
            description = "Retrieves statistics about zoo capacity and occupancy")
    public ApiResponse<Map<String, Object>> getCapacityStatistics() {
        log.info("Getting capacity statistics");

        StatisticsDto fullStats = statisticsService.getZooStatistics();

        Map<String, Object> capacityStats = new HashMap<>();
        capacityStats.put("totalCapacity", fullStats.getTotalCapacity());
        capacityStats.put("usedCapacity", fullStats.getUsedCapacity());
        capacityStats.put("availableCapacity", fullStats.getTotalCapacity() - fullStats.getUsedCapacity());
        capacityStats.put("occupancyRate", String.format("%.2f%%", fullStats.getOccupancyRate()));
        capacityStats.put("emptyEnclosures", fullStats.getEmptyEnclosures());
        capacityStats.put("enclosuresByType", fullStats.getEnclosuresByType());

        return ApiResponse.success(capacityStats);
    }
}
