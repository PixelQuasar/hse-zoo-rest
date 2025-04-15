package com.zoo.hsezoorest.domain.repository;

import com.zoo.hsezoorest.domain.model.animal.AnimalId;
import com.zoo.hsezoorest.domain.model.feeding.FeedingId;
import com.zoo.hsezoorest.domain.model.feeding.Feeding;
import com.zoo.hsezoorest.domain.model.feeding.FoodType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface FeedingRepository {
    Feeding save(Feeding feedingSchedule);

    Optional<Feeding> findById(FeedingId id);

    List<Feeding> findAll();

    List<Feeding> findByAnimalId(AnimalId animalId);

    List<Feeding> findByTimeRange(LocalTime startTime, LocalTime endTime);

    List<Feeding> findByFoodType(FoodType foodType);

    List<Feeding> findPendingFeedings(LocalTime currentTime);

    List<Feeding> findCompletedFeedingsByDate(LocalDate date);

    boolean deleteById(FeedingId id);

    int deleteByAnimalId(AnimalId animalId);

    void deleteAll();

    boolean existsById(FeedingId id);

    long count();
}
