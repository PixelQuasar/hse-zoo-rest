package com.zoo.hsezoorest.domain.model.animal;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

@Getter
@EqualsAndHashCode
public class AnimalId {
    private final String value;

    private AnimalId(String value) {
        this.value = value;
    }

    public static AnimalId create() {
        return new AnimalId(UUID.randomUUID().toString());
    }

    public static AnimalId of(String id) {
        return new AnimalId(id);
    }

    @Override
    public String toString() {
        return value;
    }
}
