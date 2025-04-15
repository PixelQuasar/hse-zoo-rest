package com.zoo.hsezoorest.domain.model.animal;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class FavoriteFood {
    private final String value;

    private FavoriteFood(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Favorite food cannot be empty");
        }
        this.value = value;
    }

    public static FavoriteFood of(String food) {
        return new FavoriteFood(food);
    }

    @Override
    public String toString() {
        return value;
    }
}
