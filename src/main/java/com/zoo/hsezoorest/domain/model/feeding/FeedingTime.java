package com.zoo.hsezoorest.domain.model.feeding;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


@Getter
@EqualsAndHashCode
public class FeedingTime {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final LocalTime time;

    private FeedingTime(LocalTime time) {
        this.time = time;
    }

    public static FeedingTime of(int hour, int minute) {
        return new FeedingTime(LocalTime.of(hour, minute));
    }

    public static FeedingTime of(LocalTime time) {
        return new FeedingTime(time);
    }

    public boolean isFeedingTime(LocalTime currentTime) {
        return currentTime.equals(time) || currentTime.isAfter(time);
    }

    @Override
    public String toString() {
        return time.format(TIME_FORMATTER);
    }
}
