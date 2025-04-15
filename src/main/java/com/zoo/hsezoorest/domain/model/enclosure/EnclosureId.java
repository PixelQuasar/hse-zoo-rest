package com.zoo.hsezoorest.domain.model.enclosure;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

@Getter
@EqualsAndHashCode
public class EnclosureId {
    private final String value;

    private EnclosureId(String value) {
        this.value = value;
    }

    public static EnclosureId create() {
        return new EnclosureId(UUID.randomUUID().toString());
    }

    public static EnclosureId of(String id) {
        return new EnclosureId(id);
    }

    @Override
    public String toString() {
        return value;
    }
}
