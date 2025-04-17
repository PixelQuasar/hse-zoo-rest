package com.zoo.hsezoorest.presentation.controller;

import com.zoo.hsezoorest.domain.model.animal.Animal;
import com.zoo.hsezoorest.domain.model.animal.HealthStatus;
import com.zoo.hsezoorest.domain.model.enclosure.Capacity;
import com.zoo.hsezoorest.domain.model.enclosure.Enclosure;
import com.zoo.hsezoorest.domain.model.enclosure.EnclosureId;
import com.zoo.hsezoorest.domain.model.enclosure.EnclosureType;
import com.zoo.hsezoorest.domain.repository.EnclosureRepository;
import com.zoo.hsezoorest.presentation.request.EnclosureRequest;
import com.zoo.hsezoorest.presentation.response.ApiResponse;
import com.zoo.hsezoorest.presentation.response.EnclosureResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/enclosures")
@RequiredArgsConstructor
@Tag(name = "Enclosure Management", description = "APIs for managing zoo enclosures")
public class EnclosureController {

    private final EnclosureRepository enclosureRepository;

    @GetMapping
    @Operation(summary = "Get all enclosures", description = "Retrieves a list of all enclosures in the zoo")
    public ApiResponse<List<EnclosureResponse>> getAllEnclosures() {
        log.info("Getting all enclosures");
        List<EnclosureResponse> enclosures = enclosureRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return ApiResponse.success(enclosures);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get enclosure by ID", description = "Retrieves a specific enclosure by its ID")
    public ApiResponse<EnclosureResponse> getEnclosureById(@PathVariable String id) {
        log.info("Getting enclosure with ID: {}", id);
        Enclosure enclosure = enclosureRepository.findById(EnclosureId.of(id))
                .orElseThrow(() -> new EntityNotFoundException("Enclosure not found with ID: " + id));

        return ApiResponse.success(convertToResponse(enclosure));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new enclosure", description = "Adds a new enclosure to the zoo")
    public ApiResponse<EnclosureResponse> createEnclosure(@Valid @RequestBody EnclosureRequest request) {
        log.info("Creating new enclosure of type: {}", request.getType());

        Enclosure enclosure = new Enclosure(
                EnclosureId.create(),
                request.getType(),
                Capacity.of(request.getCapacity())
        );

        Enclosure savedEnclosure = enclosureRepository.save(enclosure);

        return ApiResponse.success(
                "Enclosure created successfully",
                convertToResponse(savedEnclosure)
        );
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an enclosure", description = "Updates an existing enclosure's information")
    public ApiResponse<EnclosureResponse> updateEnclosure(
            @PathVariable String id,
            @Valid @RequestBody EnclosureRequest request) {
        log.info("Updating enclosure with ID: {}", id);

        Enclosure enclosure = enclosureRepository.findById(EnclosureId.of(id))
                .orElseThrow(() -> new EntityNotFoundException("Enclosure not found with ID: " + id));

        Enclosure updatedEnclosure = new Enclosure(
                enclosure.getId(),
                request.getType(),
                Capacity.of(request.getCapacity())
        );

        for (Animal animal : enclosure.getAnimals()) {
            updatedEnclosure.addAnimal(animal);
        }

        if (!enclosure.isClean()) {
            updatedEnclosure.markAsDirty();
        }

        Enclosure savedEnclosure = enclosureRepository.save(updatedEnclosure);

        return ApiResponse.success(
                "Enclosure updated successfully",
                convertToResponse(savedEnclosure)
        );
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an enclosure", description = "Removes an enclosure from the zoo")
    public ApiResponse<Void> deleteEnclosure(@PathVariable String id) {
        log.info("Deleting enclosure with ID: {}", id);

        Enclosure enclosure = enclosureRepository.findById(EnclosureId.of(id))
                .orElseThrow(() -> new EntityNotFoundException("Enclosure not found with ID: " + id));

        if (!enclosure.getAnimals().isEmpty()) {
            throw new IllegalStateException("Cannot delete enclosure that contains animals");
        }

        enclosureRepository.deleteById(enclosure.getId());
        return ApiResponse.success("Enclosure deleted successfully", null);
    }

    @PostMapping("/{id}/clean")
    @Operation(summary = "Clean enclosure", description = "Marks an enclosure as clean")
    public ApiResponse<EnclosureResponse> cleanEnclosure(@PathVariable String id) {
        log.info("Cleaning enclosure with ID: {}", id);

        Enclosure enclosure = enclosureRepository.findById(EnclosureId.of(id))
                .orElseThrow(() -> new EntityNotFoundException("Enclosure not found with ID: " + id));

        enclosure.clean();
        enclosureRepository.save(enclosure);

        return ApiResponse.success(
                "Enclosure has been cleaned",
                convertToResponse(enclosure)
        );
    }

    @GetMapping("/available")
    @Operation(summary = "Get available enclosures", description = "Retrieves enclosures with available space")
    public ApiResponse<List<EnclosureResponse>> getAvailableEnclosures() {
        log.info("Getting available enclosures");

        List<EnclosureResponse> availableEnclosures = enclosureRepository.findAvailableEnclosures().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return ApiResponse.success(availableEnclosures);
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Get enclosures by type", description = "Retrieves enclosures of a specific type")
    public ApiResponse<List<EnclosureResponse>> getEnclosuresByType(@PathVariable String type) {
        log.info("Getting enclosures of type: {}", type);

        try {

            EnclosureType enclosureType = EnclosureType.valueOf(type.toUpperCase());

            List<EnclosureResponse> enclosures = enclosureRepository.findByType(enclosureType).stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());

            return ApiResponse.success(enclosures);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid enclosure type: " + type);
        }
    }

    private EnclosureResponse convertToResponse(Enclosure enclosure) {
        List<EnclosureResponse.AnimalInfo> animals = enclosure.getAnimals().stream()
                .map(animal -> EnclosureResponse.AnimalInfo.builder()
                        .id(animal.getId().getValue())
                        .name(animal.getName())
                        .species(animal.getSpecies().getValue())
                        .status(mapHealthStatus(animal.getHealthStatus()))
                        .build())
                .collect(Collectors.toList());

        return EnclosureResponse.builder()
                .id(enclosure.getId().getValue())
                .type(enclosure.getType())
                .capacity(enclosure.getCapacity().getMaximum())
                .currentAnimalCount(enclosure.getCurrentAnimalCount())
                .remainingCapacity(enclosure.getRemainingCapacity())
                .isClean(enclosure.isClean())
                .lastCleaningTime(enclosure.getLastCleaningTime())
                .animals(animals)
                .build();
    }


    private EnclosureResponse.AnimalInfo.HealthStatus mapHealthStatus(
            HealthStatus status) {
        switch (status) {
            case HEALTHY:
                return EnclosureResponse.AnimalInfo.HealthStatus.HEALTHY;
            case SICK:
                return EnclosureResponse.AnimalInfo.HealthStatus.SICK;
            case UNDER_OBSERVATION:
                return EnclosureResponse.AnimalInfo.HealthStatus.UNDER_OBSERVATION;
            default:
                return EnclosureResponse.AnimalInfo.HealthStatus.HEALTHY;
        }
    }
}
