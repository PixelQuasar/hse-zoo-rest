package com.zoo.hsezoorest.presentation.controller;

import com.zoo.hsezoorest.application.service.FeedingOrganizationService;
import com.zoo.hsezoorest.domain.model.animal.AnimalId;
import com.zoo.hsezoorest.domain.model.feeding.FeedingId;
import com.zoo.hsezoorest.domain.model.feeding.Feeding;
import com.zoo.hsezoorest.domain.model.feeding.FeedingTime;
import com.zoo.hsezoorest.domain.repository.FeedingRepository;
import com.zoo.hsezoorest.presentation.request.FeedingRequest;
import com.zoo.hsezoorest.presentation.response.ApiResponse;
import com.zoo.hsezoorest.presentation.response.FeedingResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/feeding-schedules")
@RequiredArgsConstructor
@Tag(name = "Feeding Management", description = "APIs for managing feeding schedules")
public class FeedingController {

    private final FeedingRepository feedingScheduleRepository;
    private final FeedingOrganizationService feedingService;

    @GetMapping
    @Operation(summary = "Get all feeding schedules",
            description = "Retrieves a list of all feeding schedules in the zoo")
    public ApiResponse<List<FeedingResponse>> getAllFeedingSchedules() {
        log.info("Getting all feeding schedules");
        List<FeedingResponse> schedules = feedingScheduleRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return ApiResponse.success(schedules);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get feeding schedule by ID",
            description = "Retrieves a specific feeding schedule by its ID")
    public ApiResponse<FeedingResponse> getFeedingScheduleById(@PathVariable String id) {
        log.info("Getting feeding schedule with ID: {}", id);
        Feeding schedule = feedingScheduleRepository.findById(FeedingId.of(id))
                .orElseThrow(() -> new EntityNotFoundException("Feeding schedule not found with ID: " + id));

        return ApiResponse.success(convertToResponse(schedule));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new feeding schedule",
            description = "Adds a new feeding schedule for an animal")
    public ApiResponse<FeedingResponse> createFeedingSchedule(@Valid @RequestBody FeedingRequest request) {
        log.info("Creating new feeding schedule for animal: {}", request.getAnimalId());

        Feeding schedule = feedingService.createFeedingSchedule(
                AnimalId.of(request.getAnimalId()),
                FeedingTime.of(request.getFeedingTime()),
                request.getFoodType()
        );

        return ApiResponse.success(
                "Feeding schedule created successfully",
                convertToResponse(schedule)
        );
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a feeding schedule",
            description = "Updates an existing feeding schedule")
    public ApiResponse<FeedingResponse> updateFeedingSchedule(
            @PathVariable String id,
            @Valid @RequestBody FeedingRequest request) {
        log.info("Updating feeding schedule with ID: {}", id);

        Feeding schedule = feedingScheduleRepository.findById(FeedingId.of(id))
                .orElseThrow(() -> new EntityNotFoundException("Feeding schedule not found with ID: " + id));

        // Update feeding time and food type
        schedule.changeFeedingTime(FeedingTime.of(request.getFeedingTime()));
        schedule.changeFoodType(request.getFoodType());

        // If animal ID is different, we need to create a new schedule
        if (!schedule.getAnimal().getId().getValue().equals(request.getAnimalId())) {
            log.info("Animal ID changed, creating new feeding schedule");
            return createFeedingSchedule(request);
        }

        Feeding savedSchedule = feedingScheduleRepository.save(schedule);

        return ApiResponse.success(
                "Feeding schedule updated successfully",
                convertToResponse(savedSchedule)
        );
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a feeding schedule",
            description = "Removes a feeding schedule from the zoo")
    public ApiResponse<Void> deleteFeedingSchedule(@PathVariable String id) {
        log.info("Deleting feeding schedule with ID: {}", id);

        if (!feedingScheduleRepository.existsById(FeedingId.of(id))) {
            throw new EntityNotFoundException("Feeding schedule not found with ID: " + id);
        }

        feedingScheduleRepository.deleteById(FeedingId.of(id));
        return ApiResponse.success("Feeding schedule deleted successfully", null);
    }

    @PostMapping("/{id}/complete")
    @Operation(summary = "Mark feeding as completed",
            description = "Marks a feeding schedule as completed")
    public ApiResponse<FeedingResponse> completeFeedingSchedule(@PathVariable String id) {
        log.info("Marking feeding schedule with ID: {} as completed", id);

        boolean completed = feedingService.completeFeedingSchedule(FeedingId.of(id));
        Feeding schedule = feedingScheduleRepository.findById(FeedingId.of(id))
                .orElseThrow(() -> new EntityNotFoundException("Feeding schedule not found with ID: " + id));

        String message = completed ?
                "Feeding marked as completed" :
                "Feeding was already completed";

        return ApiResponse.success(message, convertToResponse(schedule));
    }

    @GetMapping("/animal/{animalId}")
    @Operation(summary = "Get feeding schedules for animal",
            description = "Retrieves all feeding schedules for a specific animal")
    public ApiResponse<List<FeedingResponse>> getFeedingSchedulesForAnimal(@PathVariable String animalId) {
        log.info("Getting feeding schedules for animal with ID: {}", animalId);

        List<FeedingResponse> schedules = feedingService
                .getFeedingSchedulesForAnimal(AnimalId.of(animalId)).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return ApiResponse.success(schedules);
    }

    @GetMapping("/pending")
    @Operation(summary = "Get pending feedings",
            description = "Retrieves feeding schedules that are due but not completed")
    public ApiResponse<List<FeedingResponse>> getPendingFeedings() {
        log.info("Getting pending feeding schedules");

        List<FeedingResponse> pendingSchedules = feedingService
                .getPendingFeedings().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return ApiResponse.success(pendingSchedules);
    }

    @GetMapping("/history")
    @Operation(summary = "Get feeding history by date",
            description = "Retrieves completed feedings for a specific date")
    public ApiResponse<List<FeedingResponse>> getFeedingHistoryByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("Getting feeding history for date: {}", date);

        List<FeedingResponse> completedSchedules = feedingService
                .getFeedingHistoryByDate(date).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return ApiResponse.success(completedSchedules);
    }

    private FeedingResponse convertToResponse(Feeding schedule) {
        String enclosureId = schedule.getAnimal().getCurrentEnclosure() != null ?
                schedule.getAnimal().getCurrentEnclosure().getId().getValue() : null;

        FeedingResponse.AnimalInfo animalInfo = FeedingResponse.AnimalInfo.builder()
                .id(schedule.getAnimal().getId().getValue())
                .name(schedule.getAnimal().getName())
                .species(schedule.getAnimal().getSpecies().getValue())
                .enclosureId(enclosureId)
                .build();

        List<String> feedingHistoryDates = schedule.getFeedingHistory().stream()
                .map(LocalDate::toString)
                .collect(Collectors.toList());

        return FeedingResponse.builder()
                .id(schedule.getId().getValue())
                .animal(animalInfo)
                .feedingTime(schedule.getFeedingTime().getTime())
                .foodType(schedule.getFoodType())
                .isCompleted(schedule.isCompleted())
                .lastFedTime(schedule.getLastFedTime())
                .feedingHistory(feedingHistoryDates)
                .build();
    }
}
