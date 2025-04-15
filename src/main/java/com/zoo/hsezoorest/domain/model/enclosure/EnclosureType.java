package com.zoo.hsezoorest.domain.model.enclosure;

import com.zoo.hsezoorest.domain.model.animal.Animal;
import com.zoo.hsezoorest.domain.model.animal.Species;

/**
 * Enum representing different types of enclosures in the zoo
 */
public enum EnclosureType {
    PREDATOR("Predator", true, false, false),
    HERBIVORE("Herbivore", false, true, false),
    AVIARY("Aviary", false, false, true),
    AQUARIUM("Aquarium", false, false, false),
    TERRARIUM("Terrarium", false, false, false),
    MIXED("Mixed", true, true, false); // A special type that can house multiple types of animals

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

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Check if this enclosure type can house a specific animal
     * @param animal The animal to check compatibility with
     * @return true if compatible, false otherwise
     */
    public boolean canHouseAnimal(Animal animal) {
        Species species = animal.getSpecies();

        // Special case for MIXED enclosures
        if (this == MIXED) {
            return true;
        }

        // Logic for other enclosure types
        if (species.isPredator() && !canHousePredators) {
            return false;
        }

        if (!species.isPredator() && !canHouseHerbivores) {
            return false;
        }

        // Additional logic could be added for birds and aquatic animals

        return true;
    }
}
