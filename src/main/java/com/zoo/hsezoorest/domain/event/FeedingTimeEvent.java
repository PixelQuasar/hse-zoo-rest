package com.zoo.hsezoorest.domain.event;

import com.zoo.hsezoorest.domain.model.animal.AnimalId;
import com.zoo.hsezoorest.domain.model.feeding.FeedingId;
import com.zoo.hsezoorest.domain.model.feeding.FoodType;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Getter
public class FeedingTimeEvent implements DomainEvent {
    private final String eventId;
    private final LocalDateTime occurredOn;
    private final FeedingId feedingId;
    private final AnimalId animalId;
    private final String animalName;
    private final String animalSpecies;
    private final LocalTime scheduledTime;
    private final FoodType foodType;
    private final String enclosureId;

    public FeedingTimeEvent(
            FeedingId feedingId,
            AnimalId animalId,
            String animalName,
            String animalSpecies,
            LocalTime scheduledTime,
            FoodType foodType,
            String enclosureId) {

        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = LocalDateTime.now();
        this.feedingId = feedingId;
        this.animalId = animalId;
        this.animalName = animalName;
        this.animalSpecies = animalSpecies;
        this.scheduledTime = scheduledTime;
        this.foodType = foodType;
        this.enclosureId = enclosureId;
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
        return "FeedingTimeEvent";
    }

    public boolean isOverdue(LocalTime currentTime) {
        return scheduledTime.isBefore(currentTime);
    }

    public String getDescription() {
        return String.format("It's time to feed %s (%s) with %s. Scheduled time: %s, Enclosure: %s",
                animalName, animalSpecies, foodType.toString(),
                scheduledTime.toString(), enclosureId);
    }
}
