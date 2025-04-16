package com.zoo.hsezoorest.domain.model.feeding;

public enum FoodType {
    MEAT("Meat"),
    FISH("Fish"),
    VEGETABLES("Vegetables"),
    FRUITS("Fruits"),
    INSECTS("Insects"),
    GRAINS("Grains"),
    HAY("Hay"),
    SEEDS("Seeds"),
    SPECIAL_DIET("Special Diet");

    private final String displayName;

    FoodType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isSuitableForCarnivores() {
        return this == MEAT || this == FISH || this == INSECTS;
    }

    public boolean isSuitableForHerbivores() {
        return this == VEGETABLES || this == FRUITS ||
                this == GRAINS || this == HAY || this == SEEDS;
    }
}
