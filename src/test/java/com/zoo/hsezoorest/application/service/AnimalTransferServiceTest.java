package com.zoo.hsezoorest.application.service;

import com.zoo.hsezoorest.domain.event.AnimalMovedEvent;
import com.zoo.hsezoorest.domain.model.animal.Animal;
import com.zoo.hsezoorest.domain.model.animal.AnimalId;
import com.zoo.hsezoorest.domain.model.animal.Species;
import com.zoo.hsezoorest.domain.model.enclosure.Enclosure;
import com.zoo.hsezoorest.domain.model.enclosure.EnclosureId;
import com.zoo.hsezoorest.domain.model.enclosure.EnclosureType;
import com.zoo.hsezoorest.domain.repository.AnimalRepository;
import com.zoo.hsezoorest.domain.repository.EnclosureRepository;
import com.zoo.hsezoorest.infrastructure.event.EventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnimalTransferServiceTest {

    @Mock
    private AnimalRepository mockAnimalRepository;
    @Mock
    private EnclosureRepository mockEnclosureRepository;
    @Mock
    private EventPublisher mockEventPublisher;

    @InjectMocks // Automatically injects mocks into the service
    private AnimalTransferService animalTransferService;

    @Mock
    private Animal mockAnimal;
    @Mock
    private Enclosure mockSourceEnclosure;
    @Mock
    private Enclosure mockTargetEnclosure;
    @Mock
    private Species mockSpecies;

    private AnimalId animalId;
    private EnclosureId sourceEnclosureId;
    private EnclosureId targetEnclosureId;

    @BeforeEach
    void setUp() {
        animalId = AnimalId.create();
        sourceEnclosureId = EnclosureId.create();
        targetEnclosureId = EnclosureId.create();

        // Remove common mock setups that are not universally needed
        // when(mockAnimal.getId()).thenReturn(animalId);
        // when(mockAnimal.getSpecies()).thenReturn(mockSpecies);
        // when(mockTargetEnclosure.getId()).thenReturn(targetEnclosureId);
    }

    @Test
    void transferAnimal_shouldTransferSuccessfully_whenValid() {
        // Arrange
        String reason = "Test transfer";
        // Add necessary stubs here
        lenient().when(mockAnimal.getId()).thenReturn(animalId);
        lenient().when(mockAnimal.getSpecies()).thenReturn(mockSpecies);
        lenient().when(mockTargetEnclosure.getId()).thenReturn(targetEnclosureId);

        when(mockAnimalRepository.findById(animalId)).thenReturn(Optional.of(mockAnimal));
        when(mockEnclosureRepository.findById(targetEnclosureId)).thenReturn(Optional.of(mockTargetEnclosure));
        when(mockAnimal.getCurrentEnclosure()).thenReturn(mockSourceEnclosure);
        when(mockSourceEnclosure.getId()).thenReturn(sourceEnclosureId);
        when(mockTargetEnclosure.hasAvailableSpace()).thenReturn(true);
        when(mockTargetEnclosure.canHouseAnimal(mockAnimal)).thenReturn(true);
        when(mockAnimal.getName()).thenReturn("Simba");
        when(mockSpecies.getValue()).thenReturn("Lion");

        // Act
        Animal resultAnimal = animalTransferService.transferAnimal(animalId, targetEnclosureId, reason);

        // Assert
        assertEquals(mockAnimal, resultAnimal);
        verify(mockAnimal).moveToEnclosure(mockTargetEnclosure); // Verify domain object method was called
        verify(mockAnimalRepository).save(mockAnimal); // Verify animal was saved

        // Verify event publication
        ArgumentCaptor<AnimalMovedEvent> eventCaptor = ArgumentCaptor.forClass(AnimalMovedEvent.class);
        verify(mockEventPublisher).publish(eventCaptor.capture());
        AnimalMovedEvent publishedEvent = eventCaptor.getValue();
        assertEquals(animalId, publishedEvent.getAnimalId());
        assertEquals(targetEnclosureId, publishedEvent.getTargetEnclosureId());
        assertEquals(sourceEnclosureId, publishedEvent.getSourceEnclosureId());
        assertEquals(reason, publishedEvent.getReason());
        assertEquals("Simba", publishedEvent.getAnimalName());
        assertEquals("Lion", publishedEvent.getAnimalSpecies());
    }

    @Test
    void transferAnimal_shouldThrowEntityNotFoundException_whenAnimalNotFound() {
        // Arrange
        // No specific stubs needed from setUp for this path
        when(mockAnimalRepository.findById(animalId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            animalTransferService.transferAnimal(animalId, targetEnclosureId, "Test");
        });
        verify(mockEnclosureRepository, never()).findById(any());
        verify(mockAnimal, never()).moveToEnclosure(any());
        verify(mockAnimalRepository, never()).save(any());
        verify(mockEventPublisher, never()).publish(any());
    }

    @Test
    void transferAnimal_shouldThrowEntityNotFoundException_whenEnclosureNotFound() {
        // Arrange
        // Add necessary stubs here
        lenient().when(mockAnimal.getId()).thenReturn(animalId); // Lenient needed as event isn't created
        lenient().when(mockAnimal.getSpecies()).thenReturn(mockSpecies); // Lenient needed
        // No need to stub mockTargetEnclosure.getId()

        when(mockAnimalRepository.findById(animalId)).thenReturn(Optional.of(mockAnimal));
        when(mockEnclosureRepository.findById(targetEnclosureId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            animalTransferService.transferAnimal(animalId, targetEnclosureId, "Test");
        });
        verify(mockAnimal, never()).moveToEnclosure(any());
        verify(mockAnimalRepository, never()).save(any());
        verify(mockEventPublisher, never()).publish(any());
    }

    @Test
    void transferAnimal_shouldThrowIllegalArgumentException_whenTargetFull() {
        // Arrange
        // Add necessary stubs here
        lenient().when(mockAnimal.getId()).thenReturn(animalId); // Lenient needed as event isn't created
        lenient().when(mockAnimal.getSpecies()).thenReturn(mockSpecies); // Lenient needed
        lenient().when(mockTargetEnclosure.getId()).thenReturn(targetEnclosureId); // Lenient needed

        when(mockAnimalRepository.findById(animalId)).thenReturn(Optional.of(mockAnimal));
        when(mockEnclosureRepository.findById(targetEnclosureId)).thenReturn(Optional.of(mockTargetEnclosure));
        when(mockTargetEnclosure.hasAvailableSpace()).thenReturn(false); // Target is full

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            animalTransferService.transferAnimal(animalId, targetEnclosureId, "Test");
        });
        verify(mockAnimal, never()).moveToEnclosure(any());
        verify(mockAnimalRepository, never()).save(any());
        verify(mockEventPublisher, never()).publish(any());
    }

    @Test
    void transferAnimal_shouldThrowIllegalArgumentException_whenIncompatible() {
        // Arrange
        // Add necessary stubs here
        lenient().when(mockAnimal.getId()).thenReturn(animalId); // Lenient needed as event isn't created
        lenient().when(mockAnimal.getSpecies()).thenReturn(mockSpecies); // Needed for error message check and potentially later
        lenient().when(mockTargetEnclosure.getId()).thenReturn(targetEnclosureId); // Lenient needed

        when(mockAnimalRepository.findById(animalId)).thenReturn(Optional.of(mockAnimal));
        when(mockEnclosureRepository.findById(targetEnclosureId)).thenReturn(Optional.of(mockTargetEnclosure));
        when(mockTargetEnclosure.hasAvailableSpace()).thenReturn(true);
        when(mockTargetEnclosure.canHouseAnimal(mockAnimal)).thenReturn(false); // Incompatible
        when(mockSpecies.getValue()).thenReturn("Tiger");
        when(mockTargetEnclosure.getType()).thenReturn(EnclosureType.HERBIVORE); // Example

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            animalTransferService.transferAnimal(animalId, targetEnclosureId, "Test");
        });
        assertTrue(exception.getMessage().contains("Tiger"));
        assertTrue(exception.getMessage().contains("HERBIVORE"));
        verify(mockAnimal, never()).moveToEnclosure(any());
        verify(mockAnimalRepository, never()).save(any());
        verify(mockEventPublisher, never()).publish(any());
    }

    @Test
    void assignToEnclosure_shouldCallTransferWithInitialAssignmentReason() {
        // Use spy to verify the call to the other method within the same class
        AnimalTransferService serviceSpy = spy(animalTransferService);

        // Arrange mocks for the transferAnimal method to run without error
        // Add necessary stubs here
        lenient().when(mockAnimal.getId()).thenReturn(animalId);
        lenient().when(mockAnimal.getSpecies()).thenReturn(mockSpecies);
        lenient().when(mockTargetEnclosure.getId()).thenReturn(targetEnclosureId);

        when(mockAnimalRepository.findById(animalId)).thenReturn(Optional.of(mockAnimal));
        when(mockEnclosureRepository.findById(targetEnclosureId)).thenReturn(Optional.of(mockTargetEnclosure));
        when(mockAnimal.getCurrentEnclosure()).thenReturn(null); // Initial assignment
        when(mockTargetEnclosure.hasAvailableSpace()).thenReturn(true);
        when(mockTargetEnclosure.canHouseAnimal(mockAnimal)).thenReturn(true);
        when(mockAnimal.getName()).thenReturn("Simba");
        when(mockSpecies.getValue()).thenReturn("Lion");

        // Act
        serviceSpy.assignToEnclosure(animalId, targetEnclosureId);

        // Assert
        verify(serviceSpy).transferAnimal(animalId, targetEnclosureId, "Initial assignment");
        verify(mockEventPublisher).publish(any(AnimalMovedEvent.class)); // Ensure event still published
    }

    // --- Tests for findSuitableEnclosures ---

    @Test
    void findSuitableEnclosures_shouldReturnEnclosures_forPredator() {
        // Arrange
        // Add necessary stubs here
        lenient().when(mockAnimal.getId()).thenReturn(animalId); // Unused if animal found
        when(mockAnimal.getSpecies()).thenReturn(mockSpecies); // Needed
        // when(mockTargetEnclosure.getId()).thenReturn(targetEnclosureId); // Not needed

        when(mockAnimalRepository.findById(animalId)).thenReturn(Optional.of(mockAnimal));
        when(mockSpecies.isPredator()).thenReturn(true);
        List<Enclosure> expectedEnclosures = Arrays.asList(mock(Enclosure.class), mock(Enclosure.class));
        when(mockEnclosureRepository.findSuitableForAnimalType("predator")).thenReturn(expectedEnclosures);

        // Act
        List<Enclosure> actualEnclosures = animalTransferService.findSuitableEnclosures(animalId);

        // Assert
        assertEquals(expectedEnclosures, actualEnclosures);
        verify(mockEnclosureRepository).findSuitableForAnimalType("predator");
    }

    @Test
    void findSuitableEnclosures_shouldReturnEnclosures_forHerbivore() {
        // Arrange
        // Add necessary stubs here
        lenient().when(mockAnimal.getId()).thenReturn(animalId); // Unused if animal found
        when(mockAnimal.getSpecies()).thenReturn(mockSpecies); // Needed
        // when(mockTargetEnclosure.getId()).thenReturn(targetEnclosureId); // Not needed

        when(mockAnimalRepository.findById(animalId)).thenReturn(Optional.of(mockAnimal));
        when(mockSpecies.isPredator()).thenReturn(false); // Herbivore
        List<Enclosure> expectedEnclosures = Arrays.asList(mock(Enclosure.class));
        when(mockEnclosureRepository.findSuitableForAnimalType("herbivore")).thenReturn(expectedEnclosures);

        // Act
        List<Enclosure> actualEnclosures = animalTransferService.findSuitableEnclosures(animalId);

        // Assert
        assertEquals(expectedEnclosures, actualEnclosures);
        verify(mockEnclosureRepository).findSuitableForAnimalType("herbivore");
    }

    @Test
    void findSuitableEnclosures_shouldReturnEmptyList_whenRepositoryReturnsEmpty() {
        // Arrange
        // Add necessary stubs here
        lenient().when(mockAnimal.getId()).thenReturn(animalId); // Unused if animal found
        when(mockAnimal.getSpecies()).thenReturn(mockSpecies); // Needed
        // when(mockTargetEnclosure.getId()).thenReturn(targetEnclosureId); // Not needed

        when(mockAnimalRepository.findById(animalId)).thenReturn(Optional.of(mockAnimal));
        when(mockSpecies.isPredator()).thenReturn(false); // Herbivore
        when(mockEnclosureRepository.findSuitableForAnimalType("herbivore")).thenReturn(Collections.emptyList());

        // Act
        List<Enclosure> actualEnclosures = animalTransferService.findSuitableEnclosures(animalId);

        // Assert
        assertTrue(actualEnclosures.isEmpty());
        verify(mockEnclosureRepository).findSuitableForAnimalType("herbivore");
    }

    @Test
    void findSuitableEnclosures_shouldThrowEntityNotFoundException_whenAnimalNotFound() {
        // Arrange
        // No specific stubs needed from setUp for this path
        when(mockAnimalRepository.findById(animalId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            animalTransferService.findSuitableEnclosures(animalId);
        });
        verify(mockEnclosureRepository, never()).findSuitableForAnimalType(anyString());
    }

    // TODO: Add tests for findSuitableEnclosures

} 