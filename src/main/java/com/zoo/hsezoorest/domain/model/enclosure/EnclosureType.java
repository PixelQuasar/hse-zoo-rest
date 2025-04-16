package com.zoo.hsezoorest.domain.model.enclosure;

import com.zoo.hsezoorest.domain.model.animal.Animal;
import com.zoo.hsezoorest.domain.model.animal.Species;
import lombok.Getter;

public enum EnclosureType {
    PREDATOR("Predator", true, false, false),
    HERBIVORE("Herbivore", false, true, false),
    AVIARY("Aviary", false, false, true),
    AQUARIUM("Aquarium", false, false, false),
    TERRARIUM("Terrarium", false, false, false),
    MIXED("Mixed", true, true, false); // A special type that can house multiple types of animals

    @Getter
    private final String displayName;
    private final boolean canHousePredators;
    private final boolean canHouseHerbivores;
    private final boolean canHouseBirds;

    EnclosureType(String displayName,
                  boolean canHousePredators,
                  boolean canHouseHerbivores,
                  boolean canHouseBirds) {
        this.displayName = displayName;
        this.canHousePredators = canHousePredators;
        this.canHouseHerbivores = canHouseHerbivores;
        this.canHouseBirds = canHouseBirds;
    }

    public boolean canHouseAnimal(Animal animal) {
        Species species = animal.getSpecies();

        if (this == MIXED) {
            return true;
        }

        if (species.isPredator() && !canHousePredators) {
            return false;
        }

        if (!species.isPredator() && !canHouseHerbivores) {
            return false;
        }

        if (species.isAvian() && !canHouseBirds) {
            return false;
        }

        return true;
    }
}
