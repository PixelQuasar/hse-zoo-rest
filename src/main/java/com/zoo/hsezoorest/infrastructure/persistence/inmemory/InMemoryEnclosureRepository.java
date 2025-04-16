package com.zoo.hsezoorest.infrastructure.persistence.inmemory;


import com.zoo.hsezoorest.domain.model.enclosure.Enclosure;
import com.zoo.hsezoorest.domain.model.enclosure.EnclosureId;
import com.zoo.hsezoorest.domain.model.enclosure.EnclosureType;
import com.zoo.hsezoorest.domain.repository.EnclosureRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class InMemoryEnclosureRepository implements EnclosureRepository {

    private final Map<String, Enclosure> enclosures = new ConcurrentHashMap<>();

    @Override
    public Enclosure save(Enclosure enclosure) {
        enclosures.put(enclosure.getId().getValue(), enclosure);
        return enclosure;
    }

    @Override
    public Optional<Enclosure> findById(EnclosureId id) {
        return Optional.ofNullable(enclosures.get(id.getValue()));
    }

    @Override
    public List<Enclosure> findAll() {
        return new ArrayList<>(enclosures.values());
    }

    @Override
    public List<Enclosure> findByType(EnclosureType type) {
        return enclosures.values().stream()
                .filter(enclosure -> enclosure.getType() == type)
                .collect(Collectors.toList());
    }

    @Override
    public List<Enclosure> findAvailableEnclosures() {
        return enclosures.values().stream()
                .filter(Enclosure::hasAvailableSpace)
                .collect(Collectors.toList());
    }

    @Override
    public List<Enclosure> findSuitableForAnimalType(String animalType) {
        boolean isPredator = animalType.equalsIgnoreCase("predator");

        return enclosures.values().stream()
                .filter(enclosure -> {
                    if (isPredator) {
                        // For predators, we need enclosures that can house predators
                        return enclosure.getType() == EnclosureType.PREDATOR ||
                                enclosure.getType() == EnclosureType.MIXED;
                    } else {
                        // For herbivores, we need enclosures that can house herbivores
                        return enclosure.getType() == EnclosureType.HERBIVORE ||
                                enclosure.getType() == EnclosureType.MIXED;
                    }
                })
                .filter(Enclosure::hasAvailableSpace)
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteById(EnclosureId id) {
        return enclosures.remove(id.getValue()) != null;
    }

    @Override
    public void deleteAll() {
        enclosures.clear();
    }

    @Override
    public boolean existsById(EnclosureId id) {
        return enclosures.containsKey(id.getValue());
    }

    @Override
    public long count() {
        return enclosures.size();
    }

    @Override
    public long countEmpty() {
        return enclosures.values().stream()
                .filter(enclosure -> enclosure.getCurrentAnimalCount() == 0)
                .count();
    }
}
