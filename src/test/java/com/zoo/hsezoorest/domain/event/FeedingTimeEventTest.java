package com.zoo.hsezoorest.domain.event;

import com.zoo.hsezoorest.domain.model.animal.AnimalId;
import com.zoo.hsezoorest.domain.model.enclosure.EnclosureId;
import com.zoo.hsezoorest.domain.model.feeding.FeedingId;
import com.zoo.hsezoorest.domain.model.feeding.FoodType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class FeedingTimeEventTest {

    private FeedingId feedingId;
    private AnimalId animalId;
    private String animalName;
    private String animalSpecies;
    private LocalTime scheduledTime;
    private FoodType foodType;
    private String enclosureIdValue;

    @BeforeEach
    void setUp() {
        feedingId = FeedingId.create();
        animalId = AnimalId.create();
        animalName = "Zara";
        animalSpecies = "Zebra";
        scheduledTime = LocalTime.of(9, 0);
        foodType = FoodType.HAY;
        enclosureIdValue = EnclosureId.create().getValue(); // Use string value as in constructor
    }

    @Test
    void constructor_shouldInitializeFieldsCorrectly() {
        FeedingTimeEvent event = new FeedingTimeEvent(
                feedingId, animalId, animalName, animalSpecies, scheduledTime, foodType, enclosureIdValue);

        assertNotNull(event.getEventId());
        assertNotNull(event.getOccurredOn());
        assertEquals(feedingId, event.getFeedingId());
        assertEquals(animalId, event.getAnimalId());
        assertEquals(animalName, event.getAnimalName());
        assertEquals(animalSpecies, event.getAnimalSpecies());
        assertEquals(scheduledTime, event.getScheduledTime());
        assertEquals(foodType, event.getFoodType());
        assertEquals(enclosureIdValue, event.getEnclosureId());
        assertEquals("FeedingTimeEvent", event.getEventType());
    }

    @Test
    void isOverdue_shouldReturnTrue_whenCurrentTimeIsAfterScheduled() {
        FeedingTimeEvent event = new FeedingTimeEvent(
                feedingId, animalId, animalName, animalSpecies, scheduledTime, foodType, enclosureIdValue);
        assertTrue(event.isOverdue(LocalTime.of(9, 1)));
        assertTrue(event.isOverdue(LocalTime.of(10, 0)));
    }

    @Test
    void isOverdue_shouldReturnFalse_whenCurrentTimeIsAtScheduled() {
        FeedingTimeEvent event = new FeedingTimeEvent(
                feedingId, animalId, animalName, animalSpecies, scheduledTime, foodType, enclosureIdValue);
        assertFalse(event.isOverdue(LocalTime.of(9, 0)));
    }

    @Test
    void isOverdue_shouldReturnFalse_whenCurrentTimeIsBeforeScheduled() {
        FeedingTimeEvent event = new FeedingTimeEvent(
                feedingId, animalId, animalName, animalSpecies, scheduledTime, foodType, enclosureIdValue);
        assertFalse(event.isOverdue(LocalTime.of(8, 59)));
    }

    @Test
    void getDescription_shouldReturnCorrectFormat() {
        FeedingTimeEvent event = new FeedingTimeEvent(
                feedingId, animalId, animalName, animalSpecies, scheduledTime, foodType, enclosureIdValue);

        String expected = String.format("It's time to feed %s (%s) with %s. Scheduled time: %s, Enclosure: %s",
                animalName, animalSpecies, foodType.toString(),
                scheduledTime.toString(), enclosureIdValue);
        assertEquals(expected, event.getDescription());
    }
} 