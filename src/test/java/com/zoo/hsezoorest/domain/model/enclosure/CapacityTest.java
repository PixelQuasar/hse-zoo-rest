package com.zoo.hsezoorest.domain.model.enclosure;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CapacityTest {

    @Test
    void of_shouldCreateCapacity_whenMaximumIsPositive() {
        int max = 5;
        Capacity capacity = Capacity.of(max);
        assertNotNull(capacity);
        assertEquals(max, capacity.getMaximum());
    }

    @Test
    void of_shouldThrowIllegalArgumentException_whenMaximumIsZero() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            Capacity.of(0);
        });
        assertEquals("Capacity must be greater than zero", exception.getMessage());
    }

    @Test
    void of_shouldThrowIllegalArgumentException_whenMaximumIsNegative() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            Capacity.of(-1);
        });
        assertEquals("Capacity must be greater than zero", exception.getMessage());
    }

    @Test
    void hasAvailableSpace_shouldReturnTrue_whenCurrentIsLessThanMaximum() {
        Capacity capacity = Capacity.of(5);
        assertTrue(capacity.hasAvailableSpace(0));
        assertTrue(capacity.hasAvailableSpace(4));
    }

    @Test
    void hasAvailableSpace_shouldReturnFalse_whenCurrentIsEqualToMaximum() {
        Capacity capacity = Capacity.of(5);
        assertFalse(capacity.hasAvailableSpace(5));
    }

    @Test
    void hasAvailableSpace_shouldReturnFalse_whenCurrentIsGreaterThanMaximum() {
        Capacity capacity = Capacity.of(5);
        // This scenario shouldn't technically happen if addAnimal checks correctly,
        // but the method should still handle it gracefully.
        assertFalse(capacity.hasAvailableSpace(6));
    }

    @Test
    void remainingCapacity_shouldReturnCorrectDifference() {
        Capacity capacity = Capacity.of(10);
        assertEquals(10, capacity.remainingCapacity(0));
        assertEquals(5, capacity.remainingCapacity(5));
        assertEquals(0, capacity.remainingCapacity(10));
        // Test potential edge case (though current > max shouldn't occur)
        assertEquals(-1, capacity.remainingCapacity(11));
    }

    @Test
    void equalsAndHashCode_shouldWorkCorrectly() {
        Capacity capacity1 = Capacity.of(10);
        Capacity capacity2 = Capacity.of(10);
        Capacity capacity3 = Capacity.of(20);

        assertEquals(capacity1, capacity2);
        assertNotEquals(capacity1, capacity3);
        assertNotEquals(capacity2, capacity3);

        assertEquals(capacity1.hashCode(), capacity2.hashCode());
        // Hash codes are not guaranteed to be different for non-equal objects,
        // but they usually are. We won't assert inequality here.
    }

    @Test
    void toString_shouldReturnMaximumAsString() {
        Capacity capacity = Capacity.of(15);
        assertEquals("15", capacity.toString());
    }
} 