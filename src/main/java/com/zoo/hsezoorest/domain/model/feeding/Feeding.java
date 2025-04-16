package com.zoo.hsezoorest.domain.model.feeding;

import com.zoo.hsezoorest.domain.model.animal.Animal;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Feeding {
    private final FeedingId id;
    private final Animal animal;
    private FeedingTime feedingTime;
    private FoodType foodType;
    private boolean isCompleted;
    private LocalDateTime lastFedTime;
    private final List<LocalDate> feedingHistory;

    public Feeding(FeedingId id, Animal animal, FeedingTime feedingTime, FoodType foodType) {
        this.id = id;
        this.animal = animal;
        this.feedingTime = feedingTime;
        this.foodType = foodType;
        this.isCompleted = false;
        this.feedingHistory = new ArrayList<>();
    }

    public void changeFeedingTime(FeedingTime newFeedingTime) {
        this.feedingTime = newFeedingTime;
    }

    public void changeFoodType(FoodType newFoodType) {
        this.foodType = newFoodType;
    }

    public boolean markAsCompleted() {
        if (isCompleted) {
            return false;
        }

        this.isCompleted = true;
        this.lastFedTime = LocalDateTime.now();
        this.feedingHistory.add(LocalDate.now());
        return true;
    }

    public void resetCompletionStatus() {
        this.isCompleted = false;
    }

    public boolean isTimeToFeed(LocalTime currentTime) {
        return !isCompleted && feedingTime.isFeedingTime(currentTime);
    }

    public List<LocalDate> getFeedingHistory() {
        return new ArrayList<>(feedingHistory);
    }

    public boolean isFoodSuitableForAnimal() {
        boolean isPredator = animal.getSpecies().isPredator();

        if (isPredator && !foodType.isSuitableForCarnivores()) {
            return false;
        }

        if (!isPredator && !foodType.isSuitableForHerbivores()) {
            return false;
        }

        return true;
    }
}
