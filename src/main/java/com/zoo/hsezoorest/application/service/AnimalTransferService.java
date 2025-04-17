package com.zoo.hsezoorest.application.service;

import com.zoo.hsezoorest.domain.event.AnimalMovedEvent;
import com.zoo.hsezoorest.domain.model.animal.Animal;
import com.zoo.hsezoorest.domain.model.animal.AnimalId;
import com.zoo.hsezoorest.domain.model.enclosure.Enclosure;
import com.zoo.hsezoorest.domain.model.enclosure.EnclosureId;
import com.zoo.hsezoorest.domain.repository.AnimalRepository;
import com.zoo.hsezoorest.domain.repository.EnclosureRepository;
import com.zoo.hsezoorest.infrastructure.event.EventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Slf4j
@Service
public class AnimalTransferService {

    private final AnimalRepository animalRepository;
    private final EnclosureRepository enclosureRepository;
    private final EventPublisher eventPublisher;

    public AnimalTransferService(AnimalRepository animalRepository,
                                 EnclosureRepository enclosureRepository,
                                 EventPublisher eventPublisher) {
        this.animalRepository = animalRepository;
        this.enclosureRepository = enclosureRepository;
        this.eventPublisher = eventPublisher;
    }

    public Animal transferAnimal(AnimalId animalId, EnclosureId targetEnclosureId, String reason) {
        log.info("Transferring animal {} to enclosure {}", animalId.getValue(), targetEnclosureId.getValue());

        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new EntityNotFoundException("Animal not found: " + animalId.getValue()));

        Enclosure targetEnclosure = enclosureRepository.findById(targetEnclosureId)
                .orElseThrow(() -> new EntityNotFoundException("Enclosure not found: " + targetEnclosureId.getValue()));

        Enclosure sourceEnclosure = animal.getCurrentEnclosure();
        EnclosureId sourceEnclosureId = sourceEnclosure != null ? sourceEnclosure.getId() : null;

        if (!targetEnclosure.hasAvailableSpace()) {
            throw new IllegalArgumentException("Target enclosure is at full capacity");
        }

        if (!targetEnclosure.canHouseAnimal(animal)) {
            throw new IllegalArgumentException(
                    "Animal type " + animal.getSpecies().getValue() +
                            " is not compatible with enclosure type " + targetEnclosure.getType());
        }

        animal.moveToEnclosure(targetEnclosure);
        animalRepository.save(animal);

        AnimalMovedEvent event = new AnimalMovedEvent(
                animal.getId(),
                animal.getName(),
                animal.getSpecies().getValue(),
                sourceEnclosureId,
                targetEnclosure.getId(),
                reason
        );
        eventPublisher.publish(event);

        log.info("Animal transferred successfully: {}", event.getDescription());
        return animal;
    }

    public Animal assignToEnclosure(AnimalId animalId, EnclosureId enclosureId) {
        return transferAnimal(animalId, enclosureId, "Initial assignment");
    }

    public List<Enclosure> findSuitableEnclosures(AnimalId animalId) {
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new EntityNotFoundException("Animal not found: " + animalId.getValue()));

        String animalType = animal.getSpecies().isPredator() ? "predator" : "herbivore";
        return enclosureRepository.findSuitableForAnimalType(animalType);
    }
}
