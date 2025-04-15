package com.zoo.hsezoorest.domain.model.enclosure;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Capacity {
    private final int maximum;

    private Capacity(int maximum) {
        if (maximum <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than zero");
        }
        this.maximum = maximum;
    }

    public static Capacity of(int maximum) {
        return new Capacity(maximum);
    }

    public boolean hasAvailableSpace(int current) {
        return current < maximum;
    }

    public int remainingCapacity(int current) {
        return maximum - current;
    }

    @Override
    public String toString() {
        return String.valueOf(maximum);
    }
}
