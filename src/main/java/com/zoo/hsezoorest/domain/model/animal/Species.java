package com.zoo.hsezoorest.domain.model.animal;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Species {
    private final String value;
    private final boolean isPredator;

    private Species(String value, boolean isPredator) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Species name cannot be empty");
        }
        this.value = value;
        this.isPredator = isPredator;
    }

    public static Species predator(String name) {
        return new Species(name, true);
    }

    public static Species herbivore(String name) {
        return new Species(name, false);
    }

    @Override
    public String toString() {
        return value + (isPredator ? " (Predator)" : " (Herbivore)");
    }
}
