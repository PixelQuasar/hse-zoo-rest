package com.zoo.hsezoorest.domain.model.enclosure;

import com.zoo.hsezoorest.domain.model.animal.Animal;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class Enclosure {
    private final EnclosureId id;
    private final EnclosureType type;
    private final Capacity capacity;
    private final List<Animal> animals;
    private LocalDateTime lastCleaningTime;
    private boolean isClean;

    public Enclosure(EnclosureId id, EnclosureType type, Capacity capacity) {
        this.id = id;
        this.type = type;
        this.capacity = capacity;
        this.animals = new ArrayList<>();
        this.lastCleaningTime = LocalDateTime.now();
        this.isClean = true;
    }

    public void addAnimal(Animal animal) {
        if (!hasAvailableSpace()) {
            throw new IllegalStateException("Enclosure is at full capacity");
        }

        if (!canHouseAnimal(animal)) {
            throw new IllegalArgumentException(
                    "This enclosure type is not suitable for " + animal.getSpecies().getValue()
            );
        }

        animals.add(animal);
    }

    public void removeAnimal(Animal animal) {
        animals.remove(animal);
    }

    public void clean() {
        this.lastCleaningTime = LocalDateTime.now();
        this.isClean = true;
    }

    public void markAsDirty() {
        this.isClean = false;
    }

    public boolean hasAvailableSpace() {
        return capacity.hasAvailableSpace(animals.size());
    }

    public int getCurrentAnimalCount() {
        return animals.size();
    }

    public int getRemainingCapacity() {
        return capacity.remainingCapacity(animals.size());
    }

    public List<Animal> getAnimals() {
        return Collections.unmodifiableList(animals);
    }

    public boolean canHouseAnimal(Animal animal) {
        return type.canHouseAnimal(animal);
    }
}
