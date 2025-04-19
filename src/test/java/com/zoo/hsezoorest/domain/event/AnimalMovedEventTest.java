package com.zoo.hsezoorest.domain.event;

import com.zoo.hsezoorest.domain.model.animal.AnimalId;
import com.zoo.hsezoorest.domain.model.enclosure.EnclosureId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnimalMovedEventTest {

    private AnimalId animalId;
    private EnclosureId sourceEnclosureId;
    private EnclosureId targetEnclosureId;
    private String animalName;
    private String animalSpecies;
    private String reason;

    @BeforeEach
    void setUp() {
        animalId = AnimalId.create();
        sourceEnclosureId = EnclosureId.create();
        targetEnclosureId = EnclosureId.create();
        animalName = "Leo";
        animalSpecies = "Lion";
        reason = "Routine transfer";
    }

    @Test
    void constructor_shouldInitializeFieldsCorrectly() {
        AnimalMovedEvent event = new AnimalMovedEvent(
                animalId, animalName, animalSpecies, sourceEnclosureId, targetEnclosureId, reason);

        assertNotNull(event.getEventId());
        assertNotNull(event.getOccurredOn());
        assertEquals(animalId, event.getAnimalId());
        assertEquals(animalName, event.getAnimalName());
        assertEquals(animalSpecies, event.getAnimalSpecies());
        assertEquals(sourceEnclosureId, event.getSourceEnclosureId());
        assertEquals(targetEnclosureId, event.getTargetEnclosureId());
        assertEquals(reason, event.getReason());
        assertEquals("AnimalMovedEvent", event.getEventType());
    }

    @Test
    void isInitialAssignment_shouldReturnTrue_whenSourceIsNull() {
        AnimalMovedEvent event = new AnimalMovedEvent(
                animalId, animalName, animalSpecies, null, targetEnclosureId, reason);
        assertTrue(event.isInitialAssignment());
    }

    @Test
    void isInitialAssignment_shouldReturnFalse_whenSourceIsNotNull() {
        AnimalMovedEvent event = new AnimalMovedEvent(
                animalId, animalName, animalSpecies, sourceEnclosureId, targetEnclosureId, reason);
        assertFalse(event.isInitialAssignment());
    }

    @Test
    void getDescription_shouldReturnCorrectFormat_forInitialAssignment() {
        AnimalMovedEvent event = new AnimalMovedEvent(
                animalId, animalName, animalSpecies, null, targetEnclosureId, "First placement");

        String expected = String.format("Animal %s (%s) was assigned to enclosure %s. Reason: %s",
                animalName, animalSpecies, targetEnclosureId.getValue(), "First placement");
        assertEquals(expected, event.getDescription());
    }

    @Test
    void getDescription_shouldReturnCorrectFormat_forRegularMove() {
        AnimalMovedEvent event = new AnimalMovedEvent(
                animalId, animalName, animalSpecies, sourceEnclosureId, targetEnclosureId, reason);

        String expected = String.format("Animal %s (%s) was moved from enclosure %s to enclosure %s. Reason: %s",
                animalName, animalSpecies, sourceEnclosureId.getValue(),
                targetEnclosureId.getValue(), reason);
        assertEquals(expected, event.getDescription());
    }
} 