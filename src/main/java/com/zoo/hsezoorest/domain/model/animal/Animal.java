package com.zoo.hsezoorest.domain.model.animal;

import com.zoo.hsezoorest.domain.model.enclosure.Enclosure;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class Animal {
    private final AnimalId id;
    private final Species species;
    private final String name;
    private final LocalDate birthDate;
    private final Gender gender;
    private FavoriteFood favoriteFood;
    private HealthStatus healthStatus;
    private Enclosure currentEnclosure;

    public Animal(AnimalId id, Species species, String name, LocalDate birthDate,
                  Gender gender, FavoriteFood favoriteFood) {
        this.id = id;
        this.species = species;
        this.name = name;
        this.birthDate = birthDate;
        this.gender = gender;
        this.favoriteFood = favoriteFood;
        this.healthStatus = HealthStatus.HEALTHY;
    }

    public boolean feed(String foodType) {
        boolean isFavoriteFood = favoriteFood.getValue().equals(foodType);
        return isFavoriteFood;
    }

    public boolean heal() {
        if (healthStatus == HealthStatus.SICK) {
            this.healthStatus = HealthStatus.HEALTHY;
            return true;
        }
        return false;
    }

    public void markAsSick() {
        this.healthStatus = HealthStatus.SICK;
    }

    public void moveToEnclosure(Enclosure newEnclosure) {
        if (!newEnclosure.canHouseAnimal(this)) {
            throw new IllegalArgumentException(
                    "Cannot move " + species.getValue() + " to enclosure of type " +
                            newEnclosure.getType()
            );
        }

        Enclosure oldEnclosure = this.currentEnclosure;
        if (oldEnclosure != null) {
            oldEnclosure.removeAnimal(this);
        }

        this.currentEnclosure = newEnclosure;
        newEnclosure.addAnimal(this);
    }

    public void changeFavoriteFood(FavoriteFood newFavoriteFood) {
        this.favoriteFood = newFavoriteFood;
    }
}
