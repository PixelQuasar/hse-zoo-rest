package com.zoo.hsezoorest.domain.model.feeding;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

class FoodTypeTest {

    @ParameterizedTest
    @EnumSource(value = FoodType.class, names = {"MEAT", "FISH", "INSECTS"})
    void isSuitableForCarnivores_shouldReturnTrue_forCarnivoreFoods(FoodType foodType) {
        assertTrue(foodType.isSuitableForCarnivores());
    }

    @ParameterizedTest
    @EnumSource(value = FoodType.class, names = {"VEGETABLES", "FRUITS", "GRAINS", "HAY", "SEEDS", "SPECIAL_DIET"})
    void isSuitableForCarnivores_shouldReturnFalse_forNonCarnivoreFoods(FoodType foodType) {
        assertFalse(foodType.isSuitableForCarnivores());
    }

    @ParameterizedTest
    @EnumSource(value = FoodType.class, names = {"VEGETABLES", "FRUITS", "GRAINS", "HAY", "SEEDS"})
    void isSuitableForHerbivores_shouldReturnTrue_forHerbivoreFoods(FoodType foodType) {
        assertTrue(foodType.isSuitableForHerbivores());
    }

    @ParameterizedTest
    @EnumSource(value = FoodType.class, names = {"MEAT", "FISH", "INSECTS", "SPECIAL_DIET"})
    void isSuitableForHerbivores_shouldReturnFalse_forNonHerbivoreFoods(FoodType foodType) {
        assertFalse(foodType.isSuitableForHerbivores());
    }

    // Note: SPECIAL_DIET is neither carnivore nor herbivore by these checks
    @Test
    void specialDiet_shouldBeNeitherCarnivoreNorHerbivore() {
        assertFalse(FoodType.SPECIAL_DIET.isSuitableForCarnivores());
        assertFalse(FoodType.SPECIAL_DIET.isSuitableForHerbivores());
    }

    @Test
    void getDisplayName_shouldReturnCorrectName() {
        assertEquals("Meat", FoodType.MEAT.getDisplayName());
        assertEquals("Vegetables", FoodType.VEGETABLES.getDisplayName());
        assertEquals("Special Diet", FoodType.SPECIAL_DIET.getDisplayName());
        // Spot check a few, assuming the pattern holds
    }
} 