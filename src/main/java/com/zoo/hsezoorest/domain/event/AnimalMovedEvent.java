package com.zoo.hsezoorest.domain.event;

import com.zoo.hsezoorest.domain.model.animal.AnimalId;
import com.zoo.hsezoorest.domain.model.enclosure.EnclosureId;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class AnimalMovedEvent implements DomainEvent {
    private final String eventId;
    private final LocalDateTime occurredOn;
    private final AnimalId animalId;
    private final String animalName;
    private final String animalSpecies;
    private final EnclosureId sourceEnclosureId;
    private final EnclosureId targetEnclosureId;
    private final String reason;


    public AnimalMovedEvent(
            AnimalId animalId,
            String animalName,
            String animalSpecies,
            EnclosureId sourceEnclosureId,
            EnclosureId targetEnclosureId,
            String reason) {

        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = LocalDateTime.now();
        this.animalId = animalId;
        this.animalName = animalName;
        this.animalSpecies = animalSpecies;
        this.sourceEnclosureId = sourceEnclosureId;
        this.targetEnclosureId = targetEnclosureId;
        this.reason = reason;
    }

    @Override
    public String getEventId() {
        return eventId;
    }

    @Override
    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }

    @Override
    public String getEventType() {
        return "AnimalMovedEvent";
    }

    public boolean isInitialAssignment() {
        return sourceEnclosureId == null;
    }

    public String getDescription() {
        if (isInitialAssignment()) {
            return String.format("Animal %s (%s) was assigned to enclosure %s. Reason: %s",
                    animalName, animalSpecies, targetEnclosureId.getValue(), reason);
        } else {
            return String.format("Animal %s (%s) was moved from enclosure %s to enclosure %s. Reason: %s",
                    animalName, animalSpecies, sourceEnclosureId.getValue(),
                    targetEnclosureId.getValue(), reason);
        }
    }
}
