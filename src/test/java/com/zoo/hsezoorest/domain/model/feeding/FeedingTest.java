package com.zoo.hsezoorest.domain.model.feeding;

import com.zoo.hsezoorest.domain.model.animal.Animal;
import com.zoo.hsezoorest.domain.model.animal.Species;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedingTest {

    private FeedingId feedingId;
    private Feeding feeding;

    @Mock
    private Animal mockAnimal;
    @Mock
    private Species mockSpecies;
    @Mock
    private FeedingTime mockFeedingTime;
    @Mock
    private FoodType mockFoodType;

    @BeforeEach
    void setUp() {
        feedingId = FeedingId.create(); // Assuming factory method
        // Remove general stubbings from here
        // when(mockAnimal.getSpecies()).thenReturn(mockSpecies);
        // Setup mocks for food suitability check to pass by default
        // when(mockSpecies.isPredator()).thenReturn(false);
        // when(mockFoodType.isSuitableForHerbivores()).thenReturn(true);
        // when(mockFoodType.isSuitableForCarnivores()).thenReturn(false);

        feeding = new Feeding(feedingId, mockAnimal, mockFeedingTime, mockFoodType);
    }

    @Test
    void constructor_shouldInitializeFeedingCorrectly() {
        // No specific stubs needed from setUp for constructor test
        assertEquals(feedingId, feeding.getId());
        assertEquals(mockAnimal, feeding.getAnimal());
        assertEquals(mockFeedingTime, feeding.getFeedingTime());
        assertEquals(mockFoodType, feeding.getFoodType());
        assertFalse(feeding.isCompleted());
        assertNull(feeding.getLastFedTime());
        assertTrue(feeding.getFeedingHistory().isEmpty());
    }

    @Test
    void changeFeedingTime_shouldUpdateFeedingTime() {
        // No specific stubs needed from setUp
        FeedingTime newTime = mock(FeedingTime.class);
        feeding.changeFeedingTime(newTime);
        assertEquals(newTime, feeding.getFeedingTime());
    }

    @Test
    void changeFoodType_shouldUpdateFoodType() {
        // No specific stubs needed from setUp
        FoodType newFood = mock(FoodType.class);
        feeding.changeFoodType(newFood);
        assertEquals(newFood, feeding.getFoodType());
    }

    @Test
    void markAsCompleted_shouldSetCompletedTrueAndUpdateHistory_whenNotCompleted() {
        // No specific stubs needed from setUp
        assertFalse(feeding.isCompleted());
        assertTrue(feeding.getFeedingHistory().isEmpty());

        boolean result = feeding.markAsCompleted();

        assertTrue(result);
        assertTrue(feeding.isCompleted());
        assertNotNull(feeding.getLastFedTime());
        assertEquals(1, feeding.getFeedingHistory().size());
        assertEquals(LocalDate.now(), feeding.getFeedingHistory().get(0));
    }

    @Test
    void markAsCompleted_shouldReturnFalse_whenAlreadyCompleted() {
        // No specific stubs needed from setUp for this test logic
        feeding.markAsCompleted(); // Mark as completed first
        assertTrue(feeding.isCompleted());
        LocalDateTime firstFedTime = feeding.getLastFedTime();
        int historySize = feeding.getFeedingHistory().size();

        boolean result = feeding.markAsCompleted(); // Try completing again

        assertFalse(result);
        assertTrue(feeding.isCompleted());
        assertEquals(firstFedTime, feeding.getLastFedTime()); // Time should not change
        assertEquals(historySize, feeding.getFeedingHistory().size()); // History size should not change
    }

    @Test
    void resetCompletionStatus_shouldSetCompletedToFalse() {
        // No specific stubs needed from setUp
        feeding.markAsCompleted();
        assertTrue(feeding.isCompleted());

        feeding.resetCompletionStatus();

        assertFalse(feeding.isCompleted());
    }

    @Test
    void isTimeToFeed_shouldReturnTrue_whenNotCompletedAndTimeMatches() {
        // Stub is needed for mockFeedingTime
        LocalTime now = LocalTime.of(9, 0);
        when(mockFeedingTime.isFeedingTime(now)).thenReturn(true);
        feeding.resetCompletionStatus(); // Ensure not completed

        assertTrue(feeding.isTimeToFeed(now));
        verify(mockFeedingTime).isFeedingTime(now);
    }

    @Test
    void isTimeToFeed_shouldReturnFalse_whenCompleted() {
        // Stub needed for mockFeedingTime initially, but won't be called
        LocalTime now = LocalTime.of(9, 0);
        // when(mockFeedingTime.isFeedingTime(now)).thenReturn(true); // Stub is unnecessary here
        feeding.markAsCompleted(); // Ensure completed

        assertFalse(feeding.isTimeToFeed(now));
        // isFeedingTime should not be called if already completed
        verify(mockFeedingTime, never()).isFeedingTime(any(LocalTime.class));
    }

    @Test
    void isTimeToFeed_shouldReturnFalse_whenNotCompletedAndTimeDoesNotMatch() {
        // Stub is needed for mockFeedingTime
        LocalTime now = LocalTime.of(10, 0);
        when(mockFeedingTime.isFeedingTime(now)).thenReturn(false);
        feeding.resetCompletionStatus(); // Ensure not completed

        assertFalse(feeding.isTimeToFeed(now));
        verify(mockFeedingTime).isFeedingTime(now);
    }

    @Test
    void isFoodSuitableForAnimal_shouldReturnTrue_forHerbivoreAndHerbivoreFood() {
        // Add necessary stubs here
        when(mockAnimal.getSpecies()).thenReturn(mockSpecies);
        when(mockSpecies.isPredator()).thenReturn(false);
        when(mockFoodType.isSuitableForHerbivores()).thenReturn(true);

        assertTrue(feeding.isFoodSuitableForAnimal());
        verify(mockFoodType, never()).isSuitableForCarnivores();
    }

    @Test
    void isFoodSuitableForAnimal_shouldReturnFalse_forHerbivoreAndCarnivoreFood() {
        // Add necessary stubs here
        when(mockAnimal.getSpecies()).thenReturn(mockSpecies);
        when(mockSpecies.isPredator()).thenReturn(false);
        when(mockFoodType.isSuitableForHerbivores()).thenReturn(false); // Not suitable herbivore food

        assertFalse(feeding.isFoodSuitableForAnimal());
        verify(mockFoodType, never()).isSuitableForCarnivores();
    }

    @Test
    void isFoodSuitableForAnimal_shouldReturnTrue_forPredatorAndCarnivoreFood() {
        // Add necessary stubs here
        when(mockAnimal.getSpecies()).thenReturn(mockSpecies);
        when(mockSpecies.isPredator()).thenReturn(true);
        when(mockFoodType.isSuitableForCarnivores()).thenReturn(true);

        assertTrue(feeding.isFoodSuitableForAnimal());
        verify(mockFoodType, never()).isSuitableForHerbivores();
    }

    @Test
    void isFoodSuitableForAnimal_shouldReturnFalse_forPredatorAndHerbivoreFood() {
        // Add necessary stubs here
        when(mockAnimal.getSpecies()).thenReturn(mockSpecies);
        when(mockSpecies.isPredator()).thenReturn(true);
        when(mockFoodType.isSuitableForCarnivores()).thenReturn(false); // Not suitable carnivore food

        assertFalse(feeding.isFoodSuitableForAnimal());
        verify(mockFoodType, never()).isSuitableForHerbivores();
    }
} 