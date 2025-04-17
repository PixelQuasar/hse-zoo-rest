package com.zoo.hsezoorest.presentation.controller;

import com.zoo.hsezoorest.application.service.AnimalTransferService;
import com.zoo.hsezoorest.domain.model.animal.Animal;
import com.zoo.hsezoorest.domain.model.animal.AnimalId;
import com.zoo.hsezoorest.domain.model.animal.FavoriteFood;
import com.zoo.hsezoorest.domain.model.animal.Species;
import com.zoo.hsezoorest.domain.model.enclosure.EnclosureId;
import com.zoo.hsezoorest.domain.repository.AnimalRepository;
import com.zoo.hsezoorest.presentation.request.AnimalRequest;
import com.zoo.hsezoorest.presentation.request.AnimalTransferRequest;
import com.zoo.hsezoorest.presentation.response.AnimalResponse;
import com.zoo.hsezoorest.presentation.response.ApiResponse;
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
@RequestMapping("/api/animals")
@RequiredArgsConstructor
@Tag(name = "Animal Management", description = "APIs for managing zoo animals")
public class AnimalController {

    private final AnimalRepository animalRepository;
    private final AnimalTransferService animalTransferService;

    @GetMapping
    @Operation(summary = "Get all animals", description = "Retrieves a list of all animals in the zoo")
    public ApiResponse<List<AnimalResponse>> getAllAnimals() {
        log.info("Getting all animals");
        List<AnimalResponse> animals = animalRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return ApiResponse.success(animals);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get animal by ID", description = "Retrieves a specific animal by its ID")
    public ApiResponse<AnimalResponse> getAnimalById(@PathVariable String id) {
        log.info("Getting animal with ID: {}", id);
        Animal animal = animalRepository.findById(AnimalId.of(id))
                .orElseThrow(() -> new EntityNotFoundException("Animal not found with ID: " + id));

        return ApiResponse.success(convertToResponse(animal));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new animal", description = "Adds a new animal to the zoo")
    public ApiResponse<AnimalResponse> createAnimal(@Valid @RequestBody AnimalRequest request) {
        log.info("Creating new animal: {}", request.getName());

        AnimalId animalId = AnimalId.create();
        Species species = request.isPredator() ?
                Species.predator(request.getSpecies()) :
                Species.herbivore(request.getSpecies());

        Animal animal = new Animal(
                animalId,
                species,
                request.getName(),
                request.getBirthDate(),
                request.getGender(),
                FavoriteFood.of(request.getFavoriteFood())
        );

        Animal savedAnimal = animalRepository.save(animal);

        if (request.getEnclosureId() != null && !request.getEnclosureId().isEmpty()) {
            EnclosureId enclosureId = EnclosureId.of(request.getEnclosureId());
            animalTransferService.assignToEnclosure(animalId, enclosureId);
            savedAnimal = animalRepository.findById(animalId).orElseThrow();
        }

        return ApiResponse.success(
                "Animal created successfully",
                convertToResponse(savedAnimal)
        );
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an animal", description = "Updates an existing animal's information")
    public ApiResponse<AnimalResponse> updateAnimal(
            @PathVariable String id,
            @Valid @RequestBody AnimalRequest request) {
        log.info("Updating animal with ID: {}", id);

        Animal animal = animalRepository.findById(AnimalId.of(id))
                .orElseThrow(() -> new EntityNotFoundException("Animal not found with ID: " + id));

        Species species = request.isPredator() ?
                Species.predator(request.getSpecies()) :
                Species.herbivore(request.getSpecies());

        Animal updatedAnimal = new Animal(
                animal.getId(),
                species,
                request.getName(),
                request.getBirthDate(),
                request.getGender(),
                FavoriteFood.of(request.getFavoriteFood())
        );

        if (animal.getHealthStatus() != null) {
            if (animal.getHealthStatus().equals(updatedAnimal.getHealthStatus())) {
            } else if (animal.getHealthStatus().getDisplayName().equals("SICK")) {
                updatedAnimal.markAsSick();
            }
        }

        Animal savedAnimal = animalRepository.save(updatedAnimal);

        if (request.getEnclosureId() != null &&
                (animal.getCurrentEnclosure() == null ||
                        !animal.getCurrentEnclosure().getId().getValue().equals(request.getEnclosureId()))) {

            EnclosureId enclosureId = EnclosureId.of(request.getEnclosureId());
            animalTransferService.transferAnimal(animal.getId(), enclosureId, "Update requested");
            savedAnimal = animalRepository.findById(animal.getId()).orElseThrow();
        }

        return ApiResponse.success(
                "Animal updated successfully",
                convertToResponse(savedAnimal)
        );
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an animal", description = "Removes an animal from the zoo")
    public ApiResponse<Void> deleteAnimal(@PathVariable String id) {
        log.info("Deleting animal with ID: {}", id);

        if (!animalRepository.existsById(AnimalId.of(id))) {
            throw new EntityNotFoundException("Animal not found with ID: " + id);
        }

        animalRepository.deleteById(AnimalId.of(id));
        return ApiResponse.success("Animal deleted successfully", null);
    }

    @PostMapping("/{id}/heal")
    @Operation(summary = "Heal an animal", description = "Changes an animal's status to healthy")
    public ApiResponse<AnimalResponse> healAnimal(@PathVariable String id) {
        log.info("Healing animal with ID: {}", id);

        Animal animal = animalRepository.findById(AnimalId.of(id))
                .orElseThrow(() -> new EntityNotFoundException("Animal not found with ID: " + id));

        boolean wasHealed = animal.heal();
        animalRepository.save(animal);

        String message = wasHealed ?
                "Animal has been healed successfully" :
                "Animal was already healthy";

        return ApiResponse.success(message, convertToResponse(animal));
    }

    @PostMapping("/{id}/mark-sick")
    @Operation(summary = "Mark as sick", description = "Changes an animal's status to sick")
    public ApiResponse<AnimalResponse> markAsSick(@PathVariable String id) {
        log.info("Marking animal with ID: {} as sick", id);

        Animal animal = animalRepository.findById(AnimalId.of(id))
                .orElseThrow(() -> new EntityNotFoundException("Animal not found with ID: " + id));

        animal.markAsSick();
        animalRepository.save(animal);

        return ApiResponse.success(
                "Animal has been marked as sick",
                convertToResponse(animal)
        );
    }

    @PostMapping("/transfer")
    @Operation(summary = "Transfer animal", description = "Moves an animal to a different enclosure")
    public ApiResponse<AnimalResponse> transferAnimal(@Valid @RequestBody AnimalTransferRequest request) {
        log.info("Transferring animal {} to enclosure {}",
                request.getAnimalId(), request.getEnclosureId());

        String reason = request.getReason() != null && !request.getReason().isEmpty() ?
                request.getReason() : "Transfer requested";

        Animal animal = animalTransferService.transferAnimal(
                AnimalId.of(request.getAnimalId()),
                EnclosureId.of(request.getEnclosureId()),
                reason
        );

        return ApiResponse.success(
                "Animal transferred successfully",
                convertToResponse(animal)
        );
    }

    private AnimalResponse convertToResponse(Animal animal) {
        AnimalResponse.EnclosureInfo enclosureInfo = null;

        if (animal.getCurrentEnclosure() != null) {
            enclosureInfo = AnimalResponse.EnclosureInfo.builder()
                    .id(animal.getCurrentEnclosure().getId().getValue())
                    .type(animal.getCurrentEnclosure().getType().getDisplayName())
                    .build();
        }

        return AnimalResponse.builder()
                .id(animal.getId().getValue())
                .name(animal.getName())
                .species(animal.getSpecies().getValue())
                .isPredator(animal.getSpecies().isPredator())
                .birthDate(animal.getBirthDate())
                .gender(animal.getGender())
                .favoriteFood(animal.getFavoriteFood().getValue())
                .healthStatus(animal.getHealthStatus())
                .currentEnclosure(enclosureInfo)
                .build();
    }
}
