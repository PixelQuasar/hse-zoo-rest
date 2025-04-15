package com.zoo.hsezoorest.domain.repository;

import com.zoo.hsezoorest.domain.model.animal.Animal;
import com.zoo.hsezoorest.domain.model.animal.AnimalId;
import com.zoo.hsezoorest.domain.model.animal.Species;

import java.util.List;
import java.util.Optional;

public interface AnimalRepository {
    Animal save(Animal animal);

    Optional<Animal> findById(AnimalId id);

    List<Animal> findAll();

    List<Animal> findBySpecies(Species species);

    List<Animal> findByEnclosureId(String enclosureId);

    List<Animal> findSickAnimals();

    boolean deleteById(AnimalId id);

    void deleteAll();

    boolean existsById(AnimalId id);

    long count();
}
