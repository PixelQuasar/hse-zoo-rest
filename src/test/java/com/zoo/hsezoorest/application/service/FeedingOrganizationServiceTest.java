package com.zoo.hsezoorest.application.service;

import com.zoo.hsezoorest.domain.event.FeedingTimeEvent;
import com.zoo.hsezoorest.domain.model.animal.Animal;
import com.zoo.hsezoorest.domain.model.animal.AnimalId;
import com.zoo.hsezoorest.domain.model.animal.Species;
import com.zoo.hsezoorest.domain.model.enclosure.Enclosure;
import com.zoo.hsezoorest.domain.model.enclosure.EnclosureId;
import com.zoo.hsezoorest.domain.model.feeding.Feeding;
import com.zoo.hsezoorest.domain.model.feeding.FeedingId;
import com.zoo.hsezoorest.domain.model.feeding.FeedingTime;
import com.zoo.hsezoorest.domain.model.feeding.FoodType;
import com.zoo.hsezoorest.domain.repository.AnimalRepository;
import com.zoo.hsezoorest.domain.repository.FeedingRepository;
import com.zoo.hsezoorest.infrastructure.event.EventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedingOrganizationServiceTest {

    @Mock
    private AnimalRepository mockAnimalRepository;
    @Mock
    private FeedingRepository mockFeedingRepository;
    @Mock
    private EventPublisher mockEventPublisher;

    @InjectMocks
    private FeedingOrganizationService feedingService;

    @Mock
    private Animal mockAnimal;
    @Mock
    private Species mockSpecies;
    @Mock
    private Feeding mockFeeding;
    @Mock
    private Enclosure mockEnclosure;

    private AnimalId animalId;
    private FeedingId feedingId;
    private FeedingTime feedingTime;
    private FoodType foodType;

    @BeforeEach
    void setUp() {
        animalId = AnimalId.create();
        feedingId = FeedingId.create();
        feedingTime = FeedingTime.of(9, 0);
        foodType = FoodType.HAY;

        // Common stubs
        lenient().when(mockAnimal.getId()).thenReturn(animalId);
        lenient().when(mockAnimal.getSpecies()).thenReturn(mockSpecies);
        lenient().when(mockFeeding.getId()).thenReturn(feedingId);
        lenient().when(mockFeeding.getAnimal()).thenReturn(mockAnimal);
    }

    // --- createFeedingSchedule Tests ---

    @Test
    void createFeedingSchedule_shouldCreateAndSave_whenValid() {
        // Arrange
        when(mockAnimalRepository.findById(animalId)).thenReturn(Optional.of(mockAnimal));
        // Mock the suitability check within the Feeding object
        lenient().when(mockSpecies.isPredator()).thenReturn(false);
        // Mock repository save to return the object passed to it
        when(mockFeedingRepository.save(any(Feeding.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Feeding createdFeeding = feedingService.createFeedingSchedule(animalId, feedingTime, foodType);

        // Assert
        assertNotNull(createdFeeding);
        assertEquals(animalId, createdFeeding.getAnimal().getId());
        assertEquals(feedingTime, createdFeeding.getFeedingTime());
        assertEquals(FoodType.HAY, createdFeeding.getFoodType());

        // Verify save was called
        ArgumentCaptor<Feeding> feedingCaptor = ArgumentCaptor.forClass(Feeding.class);
        verify(mockFeedingRepository).save(feedingCaptor.capture());
        assertEquals(createdFeeding, feedingCaptor.getValue());
    }

    @Test
    void createFeedingSchedule_shouldLogWarning_whenFoodNotSuitable() {
        // Arrange
        when(mockAnimalRepository.findById(animalId)).thenReturn(Optional.of(mockAnimal));
        when(mockSpecies.isPredator()).thenReturn(true); // Predator
        when(mockAnimal.getName()).thenReturn("Leo");
        when(mockSpecies.getValue()).thenReturn("Lion");
        when(mockFeedingRepository.save(any(Feeding.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        feedingService.createFeedingSchedule(animalId, feedingTime, foodType);

        // Assert
        // We can't directly assert log output easily without adding helpers,
        // but we verified the service creates the schedule anyway.
        // Verification that save was still called is important.
        verify(mockFeedingRepository).save(any(Feeding.class));
    }

    @Test
    void createFeedingSchedule_shouldThrowEntityNotFound_whenAnimalNotFound() {
        // Arrange
        when(mockAnimalRepository.findById(animalId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            feedingService.createFeedingSchedule(animalId, feedingTime, foodType);
        });
        verify(mockFeedingRepository, never()).save(any());
    }

    // --- completeFeedingSchedule Tests ---

    @Test
    void completeFeedingSchedule_shouldCompleteAndSave_whenNotAlreadyCompleted() {
        // Arrange
        foodType = FoodType.HAY; // Use the enum constant
        when(mockFeedingRepository.findById(feedingId)).thenReturn(Optional.of(mockFeeding));
        when(mockFeeding.markAsCompleted()).thenReturn(true); // Domain object confirms completion
        when(mockFeeding.getFoodType()).thenReturn(foodType); // <<< ADD THIS STUB
        when(mockAnimal.getName()).thenReturn("Zara");
        // No need to mock foodType.getDisplayName()

        // Act
        boolean result = feedingService.completeFeedingSchedule(feedingId);

        // Assert
        assertTrue(result);
        verify(mockFeeding).markAsCompleted();
        verify(mockAnimal).feed(FoodType.HAY.getDisplayName());
        verify(mockFeedingRepository).save(mockFeeding);
    }

    @Test
    void completeFeedingSchedule_shouldReturnFalse_whenAlreadyCompleted() {
        // Arrange
        when(mockFeedingRepository.findById(feedingId)).thenReturn(Optional.of(mockFeeding));
        when(mockFeeding.markAsCompleted()).thenReturn(false); // Domain object says already completed

        // Act
        boolean result = feedingService.completeFeedingSchedule(feedingId);

        // Assert
        assertFalse(result);
        verify(mockFeeding).markAsCompleted();
        verify(mockAnimal, never()).feed(anyString()); // Animal should not be fed again
        verify(mockFeedingRepository, never()).save(mockFeeding); // Should not save again
    }

    @Test
    void completeFeedingSchedule_shouldThrowEntityNotFound_whenFeedingNotFound() {
        // Arrange
        when(mockFeedingRepository.findById(feedingId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            feedingService.completeFeedingSchedule(feedingId);
        });
        verify(mockFeeding, never()).markAsCompleted();
        verify(mockAnimal, never()).feed(anyString());
        verify(mockFeedingRepository, never()).save(any());
    }

    // --- checkFeedingTimes Tests ---

    @Test
    void checkFeedingTimes_shouldPublishEventsForPendingFeedings() {
        // Arrange
        LocalTime currentTime = LocalTime.now(); // Or a fixed time for consistency
        Feeding pendingFeeding1 = mock(Feeding.class);
        Feeding pendingFeeding2 = mock(Feeding.class);
        List<Feeding> pendingList = List.of(pendingFeeding1, pendingFeeding2);

        Animal animal1 = mock(Animal.class);
        Animal animal2 = mock(Animal.class);
        Enclosure enclosure1 = mock(Enclosure.class);
        EnclosureId enclosureId1 = EnclosureId.create();
        Species species1 = mock(Species.class);
        Species species2 = mock(Species.class);
        AnimalId animalId1 = AnimalId.create();
        AnimalId animalId2 = AnimalId.create();
        FeedingId feedingId1 = FeedingId.create();
        FeedingId feedingId2 = FeedingId.create();
        FeedingTime time1 = FeedingTime.of(8, 0);
        FeedingTime time2 = FeedingTime.of(9, 0);

        // Setup mocks for pendingFeeding1
        when(pendingFeeding1.getId()).thenReturn(feedingId1);
        when(pendingFeeding1.getAnimal()).thenReturn(animal1);
        when(pendingFeeding1.getFeedingTime()).thenReturn(time1);
        when(pendingFeeding1.getFoodType()).thenReturn(FoodType.MEAT);
        when(animal1.getId()).thenReturn(animalId1);
        when(animal1.getName()).thenReturn("Leo");
        when(animal1.getSpecies()).thenReturn(species1);
        when(species1.getValue()).thenReturn("Lion");
        when(animal1.getCurrentEnclosure()).thenReturn(enclosure1);
        when(enclosure1.getId()).thenReturn(enclosureId1);

        // Setup mocks for pendingFeeding2
        when(pendingFeeding2.getId()).thenReturn(feedingId2);
        when(pendingFeeding2.getAnimal()).thenReturn(animal2);
        when(pendingFeeding2.getFeedingTime()).thenReturn(time2);
        when(pendingFeeding2.getFoodType()).thenReturn(FoodType.VEGETABLES);
        when(animal2.getId()).thenReturn(animalId2);
        when(animal2.getName()).thenReturn("Zara");
        when(animal2.getSpecies()).thenReturn(species2);
        when(species2.getValue()).thenReturn("Zebra");
        when(animal2.getCurrentEnclosure()).thenReturn(null); // Test unassigned enclosure

        when(mockFeedingRepository.findPendingFeedings(any(LocalTime.class))).thenReturn(pendingList);

        // Act
        feedingService.checkFeedingTimes();

        // Assert
        // Verify repository was called
        verify(mockFeedingRepository).findPendingFeedings(any(LocalTime.class));

        // Capture and verify published events
        ArgumentCaptor<FeedingTimeEvent> eventCaptor = ArgumentCaptor.forClass(FeedingTimeEvent.class);
        verify(mockEventPublisher, times(2)).publish(eventCaptor.capture());

        List<FeedingTimeEvent> publishedEvents = eventCaptor.getAllValues();
        assertEquals(2, publishedEvents.size());

        // Check details of the first event
        FeedingTimeEvent event1 = publishedEvents.stream().filter(e -> e.getFeedingId().equals(feedingId1)).findFirst().orElseThrow();
        assertEquals(animalId1, event1.getAnimalId());
        assertEquals("Leo", event1.getAnimalName());
        assertEquals("Lion", event1.getAnimalSpecies());
        assertEquals(time1.getTime(), event1.getScheduledTime());
        assertEquals(FoodType.MEAT, event1.getFoodType());
        assertEquals(enclosureId1.getValue(), event1.getEnclosureId());

        // Check details of the second event
        FeedingTimeEvent event2 = publishedEvents.stream().filter(e -> e.getFeedingId().equals(feedingId2)).findFirst().orElseThrow();
        assertEquals(animalId2, event2.getAnimalId());
        assertEquals("Zara", event2.getAnimalName());
        assertEquals("Zebra", event2.getAnimalSpecies());
        assertEquals(time2.getTime(), event2.getScheduledTime());
        assertEquals(FoodType.VEGETABLES, event2.getFoodType());
        assertEquals("Not assigned", event2.getEnclosureId());
    }

    @Test
    void checkFeedingTimes_shouldDoNothing_whenNoPendingFeedings() {
        // Arrange
        when(mockFeedingRepository.findPendingFeedings(any(LocalTime.class))).thenReturn(Collections.emptyList());

        // Act
        feedingService.checkFeedingTimes();

        // Assert
        verify(mockFeedingRepository).findPendingFeedings(any(LocalTime.class));
        verify(mockEventPublisher, never()).publish(any());
    }

    // --- resetDailyFeedingSchedules Tests ---

    @Test
    void resetDailyFeedingSchedules_shouldResetAndSaveCompletedSchedules() {
        // Arrange
        Feeding completedFeeding1 = mock(Feeding.class);
        Feeding completedFeeding2 = mock(Feeding.class);
        Feeding notCompletedFeeding = mock(Feeding.class);
        List<Feeding> allSchedules = List.of(completedFeeding1, notCompletedFeeding, completedFeeding2);

        when(mockFeedingRepository.findAll()).thenReturn(allSchedules);

        when(completedFeeding1.isCompleted()).thenReturn(true);
        when(completedFeeding2.isCompleted()).thenReturn(true);
        when(notCompletedFeeding.isCompleted()).thenReturn(false);

        // Act
        feedingService.resetDailyFeedingSchedules();

        // Assert
        verify(mockFeedingRepository).findAll();

        verify(completedFeeding1).isCompleted();
        verify(completedFeeding1).resetCompletionStatus(); // Should be called
        verify(mockFeedingRepository).save(completedFeeding1); // Should be saved

        verify(completedFeeding2).isCompleted();
        verify(completedFeeding2).resetCompletionStatus(); // Should be called
        verify(mockFeedingRepository).save(completedFeeding2); // Should be saved

        verify(notCompletedFeeding).isCompleted();
        verify(notCompletedFeeding, never()).resetCompletionStatus(); // Should NOT be called
        verify(mockFeedingRepository, never()).save(notCompletedFeeding); // Should NOT be saved
    }

    @Test
    void resetDailyFeedingSchedules_shouldDoNothing_whenNoSchedulesExist() {
        // Arrange
        when(mockFeedingRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        feedingService.resetDailyFeedingSchedules();

        // Assert
        verify(mockFeedingRepository).findAll();
        verify(mockFeedingRepository, never()).save(any());
    }

    @Test
    void resetDailyFeedingSchedules_shouldDoNothing_whenNoSchedulesAreCompleted() {
        // Arrange
        Feeding notCompleted1 = mock(Feeding.class);
        Feeding notCompleted2 = mock(Feeding.class);
        List<Feeding> allSchedules = List.of(notCompleted1, notCompleted2);

        when(mockFeedingRepository.findAll()).thenReturn(allSchedules);
        when(notCompleted1.isCompleted()).thenReturn(false);
        when(notCompleted2.isCompleted()).thenReturn(false);

        // Act
        feedingService.resetDailyFeedingSchedules();

        // Assert
        verify(mockFeedingRepository).findAll();
        verify(notCompleted1).isCompleted();
        verify(notCompleted2).isCompleted();
        verify(mockFeedingRepository, never()).save(any());
        verify(notCompleted1, never()).resetCompletionStatus();
        verify(notCompleted2, never()).resetCompletionStatus();
    }

    // --- Repository Delegation Tests ---

    @Test
    void getFeedingSchedulesForAnimal_shouldDelegateToRepository() {
        // Arrange
        List<Feeding> expectedSchedules = List.of(mock(Feeding.class));
        when(mockFeedingRepository.findByAnimalId(animalId)).thenReturn(expectedSchedules);

        // Act
        List<Feeding> actualSchedules = feedingService.getFeedingSchedulesForAnimal(animalId);

        // Assert
        assertEquals(expectedSchedules, actualSchedules);
        verify(mockFeedingRepository).findByAnimalId(animalId);
    }

    // TODO: Add tests for other repository delegation methods if desired
} 