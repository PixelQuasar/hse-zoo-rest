package com.zoo.hsezoorest.infrastructure.persistence.inmemory;

import com.zoo.hsezoorest.domain.model.animal.Animal;
import com.zoo.hsezoorest.domain.model.animal.AnimalId;
import com.zoo.hsezoorest.domain.model.animal.HealthStatus;
import com.zoo.hsezoorest.domain.model.animal.Species;
import com.zoo.hsezoorest.domain.repository.AnimalRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class InMemoryAnimalRepository implements AnimalRepository {

    private final Map<String, Animal> animals = new ConcurrentHashMap<>();

    @Override
    public Animal save(Animal animal) {
        animals.put(animal.getId().getValue(), animal);
        return animal;
    }

    @Override
    public Optional<Animal> findById(AnimalId id) {
        return Optional.ofNullable(animals.get(id.getValue()));
    }

    @Override
    public List<Animal> findAll() {
        return new ArrayList<>(animals.values());
    }

    @Override
    public List<Animal> findBySpecies(Species species) {
        return animals.values().stream()
                .filter(animal -> animal.getSpecies().equals(species))
                .collect(Collectors.toList());
    }

    @Override
    public List<Animal> findByEnclosureId(String enclosureId) {
        return animals.values().stream()
                .filter(animal -> animal.getCurrentEnclosure() != null &&
                        animal.getCurrentEnclosure().getId().getValue().equals(enclosureId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Animal> findSickAnimals() {
        return animals.values().stream()
                .filter(animal -> animal.getHealthStatus() == HealthStatus.SICK)
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteById(AnimalId id) {
        return animals.remove(id.getValue()) != null;
    }

    @Override
    public void deleteAll() {
        animals.clear();
    }

    @Override
    public boolean existsById(AnimalId id) {
        return animals.containsKey(id.getValue());
    }

    @Override
    public long count() {
        return animals.size();
    }
}
