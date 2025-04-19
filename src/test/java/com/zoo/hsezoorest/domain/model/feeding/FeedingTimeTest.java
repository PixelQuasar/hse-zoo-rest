package com.zoo.hsezoorest.domain.model.feeding;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class FeedingTimeTest {

    @Test
    void ofInts_shouldCreateFeedingTime() {
        FeedingTime feedingTime = FeedingTime.of(9, 30);
        assertNotNull(feedingTime);
        assertEquals(LocalTime.of(9, 30), feedingTime.getTime());
    }

    @Test
    void ofLocalTime_shouldCreateFeedingTime() {
        LocalTime time = LocalTime.of(14, 0);
        FeedingTime feedingTime = FeedingTime.of(time);
        assertNotNull(feedingTime);
        assertEquals(time, feedingTime.getTime());
    }

    @Test
    void isFeedingTime_shouldReturnTrue_whenCurrentTimeIsExact() {
        FeedingTime feedingTime = FeedingTime.of(10, 0);
        assertTrue(feedingTime.isFeedingTime(LocalTime.of(10, 0)));
    }

    @Test
    void isFeedingTime_shouldReturnTrue_whenCurrentTimeIsAfter() {
        FeedingTime feedingTime = FeedingTime.of(10, 0);
        assertTrue(feedingTime.isFeedingTime(LocalTime.of(10, 1)));
        assertTrue(feedingTime.isFeedingTime(LocalTime.of(11, 0)));
    }

    @Test
    void isFeedingTime_shouldReturnFalse_whenCurrentTimeIsBefore() {
        FeedingTime feedingTime = FeedingTime.of(10, 0);
        assertFalse(feedingTime.isFeedingTime(LocalTime.of(9, 59)));
        assertFalse(feedingTime.isFeedingTime(LocalTime.of(8, 0)));
    }

    @Test
    void equalsAndHashCode_shouldWorkCorrectly() {
        FeedingTime time1 = FeedingTime.of(12, 0);
        FeedingTime time2 = FeedingTime.of(LocalTime.of(12, 0));
        FeedingTime time3 = FeedingTime.of(12, 1);

        assertEquals(time1, time2);
        assertNotEquals(time1, time3);
        assertNotEquals(time2, time3);

        assertEquals(time1.hashCode(), time2.hashCode());
    }

    @Test
    void toString_shouldReturnFormattedTime() {
        FeedingTime feedingTime = FeedingTime.of(8, 5);
        assertEquals("08:05", feedingTime.toString());

        FeedingTime feedingTime2 = FeedingTime.of(15, 45);
        assertEquals("15:45", feedingTime2.toString());
    }
} 