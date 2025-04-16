package com.zoo.hsezoorest.domain.model.animal;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Species {
    private final String value;
    private final boolean isPredator;
    private final boolean isAvian;

    private Species(String value, boolean isPredator, boolean isAvian) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Species name cannot be empty");
        }
        this.value = value;
        this.isPredator = isPredator;
        this.isAvian = isAvian;
    }

    public static Species predator(String name) {
        return new Species(name, true, false);
    }

    public static Species herbivore(String name) {
        return new Species(name, false, false);
    }

    public static Species avianPredator(String name) {
        return new Species(name, true, true);
    }

    public static Species avianHerbivore(String name) {
        return new Species(name, false, true);
    }

    @Override
    public String toString() {
        return value + (isPredator ? " (Predator)" : " (Herbivore)") + (isAvian ? " (Avian)" : "");
    }
}
