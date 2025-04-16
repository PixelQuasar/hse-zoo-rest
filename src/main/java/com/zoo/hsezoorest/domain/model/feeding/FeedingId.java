package com.zoo.hsezoorest.domain.model.feeding;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

@Getter
@EqualsAndHashCode
public class FeedingId {
    private final String value;

    private FeedingId(String value) {
        this.value = value;
    }

    public static FeedingId create() {
        return new FeedingId(UUID.randomUUID().toString());
    }

    public static FeedingId of(String id) {
        return new FeedingId(id);
    }

    @Override
    public String toString() {
        return value;
    }
}
