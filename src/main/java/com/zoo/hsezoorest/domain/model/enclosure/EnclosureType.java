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

        // Handle the special MIXED case first
        if (this == MIXED) {
            // The current MIXED definition is (true, true, false) -> PREDATOR or HERBIVORE, but not AVIAN
            // If MIXED should truly house *any* animal, its definition or this logic needs adjustment.
            // Assuming current definition (Predator/Herbivore, non-Avian) is intended:
            // return !species.isAvian(); 
            // --- OR --- If MIXED means truly anything:
             return true;
        }

        // Check specific incompatibilities
        if (species.isAvian() && !this.canHouseBirds) {
            return false; // If it's a bird but enclosure isn't for birds
        }
        if (species.isPredator() && !this.canHousePredators) {
            return false; // If it's a predator but enclosure isn't for predators
        }
        // If it's a non-predator, non-avian (i.e., a standard herbivore) and enclosure isn't for herbivores
        if (!species.isPredator() && !species.isAvian() && !this.canHouseHerbivores) { 
            return false; 
        }

        // If none of the above specific incompatibilities match, it's compatible.
        return true;
    }
}
