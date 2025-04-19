package com.zoo.hsezoorest.domain.model.enclosure;

import com.zoo.hsezoorest.domain.model.animal.Animal;
import com.zoo.hsezoorest.domain.model.animal.Species;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnclosureTest {

    private EnclosureId enclosureId;
    private Enclosure enclosure;

    @Mock
    private EnclosureType mockType;
    @Mock
    private Capacity mockCapacity;
    @Mock
    private Animal mockAnimal1;
    @Mock
    private Animal mockAnimal2;
    @Mock
    private Species mockSpecies;

    @BeforeEach
    void setUp() {
        enclosureId = EnclosureId.create(); // Assuming EnclosureId has a create() factory
        // Remove general stubbings from here
        // when(mockCapacity.hasAvailableSpace(anyInt())).thenReturn(true);
        // when(mockType.canHouseAnimal(any(Animal.class))).thenReturn(true);

        enclosure = new Enclosure(enclosureId, mockType, mockCapacity);
    }

    @Test
    void constructor_shouldInitializeEnclosureCorrectly() {
        assertEquals(enclosureId, enclosure.getId());
        assertEquals(mockType, enclosure.getType());
        assertEquals(mockCapacity, enclosure.getCapacity());
        assertTrue(enclosure.getAnimals().isEmpty());
        assertNotNull(enclosure.getLastCleaningTime());
        assertTrue(enclosure.isClean());
    }

    @Test
    void addAnimal_shouldAddAnimal_whenSuitableAndSpaceAvailable() {
        // Arrange
        when(mockCapacity.hasAvailableSpace(anyInt())).thenReturn(true); // Add stubbing here
        when(mockType.canHouseAnimal(any(Animal.class))).thenReturn(true); // Add stubbing here

        // Act
        enclosure.addAnimal(mockAnimal1);

        // Assert
        assertEquals(1, enclosure.getCurrentAnimalCount());
        assertTrue(enclosure.getAnimals().contains(mockAnimal1));
        verify(mockCapacity).hasAvailableSpace(0); // Called before adding
        verify(mockType).canHouseAnimal(mockAnimal1); // Called before adding
    }

    @Test
    void addAnimal_shouldThrowIllegalStateException_whenNoSpaceAvailable() {
        // Arrange
        when(mockCapacity.hasAvailableSpace(0)).thenReturn(false); // No space initially

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            enclosure.addAnimal(mockAnimal1);
        });
        assertEquals(0, enclosure.getCurrentAnimalCount());
        verify(mockCapacity).hasAvailableSpace(0);
        verify(mockType, never()).canHouseAnimal(any(Animal.class)); // Shouldn't check type if no space
    }

    @Test
    void addAnimal_shouldThrowIllegalArgumentException_whenNotSuitable() {
        // Arrange
        when(mockCapacity.hasAvailableSpace(anyInt())).thenReturn(true); // Add stubbing for capacity check
        when(mockType.canHouseAnimal(mockAnimal1)).thenReturn(false); // Not suitable
        when(mockAnimal1.getSpecies()).thenReturn(mockSpecies);
        when(mockSpecies.getValue()).thenReturn("Tiger"); // For error message

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            enclosure.addAnimal(mockAnimal1);
        });
        assertTrue(exception.getMessage().contains("not suitable for Tiger"));
        assertEquals(0, enclosure.getCurrentAnimalCount());
        verify(mockCapacity).hasAvailableSpace(0);
        verify(mockType).canHouseAnimal(mockAnimal1);
    }

    @Test
    void removeAnimal_shouldRemoveExistingAnimal() {
        // Arrange: Add an animal first
        when(mockCapacity.hasAvailableSpace(anyInt())).thenReturn(true); // Needed for addAnimal
        when(mockType.canHouseAnimal(any(Animal.class))).thenReturn(true); // Needed for addAnimal
        enclosure.addAnimal(mockAnimal1);
        assertEquals(1, enclosure.getCurrentAnimalCount());

        // Act
        enclosure.removeAnimal(mockAnimal1);

        // Assert
        assertEquals(0, enclosure.getCurrentAnimalCount());
        assertFalse(enclosure.getAnimals().contains(mockAnimal1));
    }

    @Test
    void removeAnimal_shouldDoNothing_whenAnimalNotInEnclosure() {
        // Arrange: Add animal1
        when(mockCapacity.hasAvailableSpace(anyInt())).thenReturn(true); // Needed for addAnimal
        when(mockType.canHouseAnimal(any(Animal.class))).thenReturn(true); // Needed for addAnimal
        enclosure.addAnimal(mockAnimal1);
        assertEquals(1, enclosure.getCurrentAnimalCount());

        // Act: Try removing a different animal
        enclosure.removeAnimal(mockAnimal2);

        // Assert
        assertEquals(1, enclosure.getCurrentAnimalCount());
        assertTrue(enclosure.getAnimals().contains(mockAnimal1));
    }

    @Test
    void clean_shouldUpdateLastCleaningTimeAndSetIsCleanToTrue() {
        // Arrange
        enclosure.markAsDirty();
        assertFalse(enclosure.isClean());
        LocalDateTime timeBeforeClean = enclosure.getLastCleaningTime();

        // Act
        enclosure.clean();

        // Assert
        assertTrue(enclosure.isClean());
        assertTrue(enclosure.getLastCleaningTime().isAfter(timeBeforeClean));
    }

    @Test
    void markAsDirty_shouldSetIsCleanToFalse() {
        // Arrange (starts clean)
        assertTrue(enclosure.isClean());

        // Act
        enclosure.markAsDirty();

        // Assert
        assertFalse(enclosure.isClean());
    }

    @Test
    void hasAvailableSpace_shouldDelegateToCapacity() {
        // Arrange
        when(mockCapacity.hasAvailableSpace(0)).thenReturn(true);

        // Act & Assert
        assertTrue(enclosure.hasAvailableSpace());
        verify(mockCapacity).hasAvailableSpace(0);

        // Arrange for false case
        when(mockCapacity.hasAvailableSpace(0)).thenReturn(false);
        assertFalse(enclosure.hasAvailableSpace());
        verify(mockCapacity, times(2)).hasAvailableSpace(0); // Called again
    }

    @Test
    void getRemainingCapacity_shouldDelegateToCapacity() {
        // Arrange
        when(mockCapacity.remainingCapacity(0)).thenReturn(5);

        // Act & Assert
        assertEquals(5, enclosure.getRemainingCapacity());
        verify(mockCapacity).remainingCapacity(0);

        // Add an animal and check again
        // Need stubs for addAnimal here
        when(mockCapacity.hasAvailableSpace(anyInt())).thenReturn(true); // Needed for addAnimal
        when(mockType.canHouseAnimal(any(Animal.class))).thenReturn(true); // Needed for addAnimal
        enclosure.addAnimal(mockAnimal1);
        when(mockCapacity.remainingCapacity(1)).thenReturn(4);
        assertEquals(4, enclosure.getRemainingCapacity());
        verify(mockCapacity).remainingCapacity(1);
    }

    @Test
    void canHouseAnimal_shouldDelegateToType() {
        // Arrange
        when(mockType.canHouseAnimal(mockAnimal1)).thenReturn(true);

        // Act & Assert
        assertTrue(enclosure.canHouseAnimal(mockAnimal1));
        verify(mockType).canHouseAnimal(mockAnimal1);

        // Arrange for false case
        when(mockType.canHouseAnimal(mockAnimal1)).thenReturn(false);
        assertFalse(enclosure.canHouseAnimal(mockAnimal1));
        verify(mockType, times(2)).canHouseAnimal(mockAnimal1); // Called again
    }

} 