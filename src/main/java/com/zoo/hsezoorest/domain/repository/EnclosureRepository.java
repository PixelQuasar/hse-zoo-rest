package com.zoo.hsezoorest.domain.repository;

import com.zoo.hsezoorest.domain.model.enclosure.Enclosure;
import com.zoo.hsezoorest.domain.model.enclosure.EnclosureId;
import com.zoo.hsezoorest.domain.model.enclosure.EnclosureType;

import java.util.List;
import java.util.Optional;

public interface EnclosureRepository {
    Enclosure save(Enclosure enclosure);

    Optional<Enclosure> findById(EnclosureId id);

    List<Enclosure> findAll();

    List<Enclosure> findByType(EnclosureType type);

    List<Enclosure> findAvailableEnclosures();

    List<Enclosure> findSuitableForAnimalType(String animalType);

    boolean deleteById(EnclosureId id);

    void deleteAll();

    boolean existsById(EnclosureId id);

    long count();

    long countEmpty();
}
