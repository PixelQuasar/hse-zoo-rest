package com.zoo.hsezoorest.infrastructure.persistence.inmemory;

import com.zoo.hsezoorest.domain.model.animal.AnimalId;
import com.zoo.hsezoorest.domain.model.feeding.FeedingId;
import com.zoo.hsezoorest.domain.model.feeding.Feeding;
import com.zoo.hsezoorest.domain.model.feeding.FoodType;
import com.zoo.hsezoorest.domain.repository.FeedingRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class InMemoryFeedingRepository implements FeedingRepository {

    private final Map<String, Feeding> feedingSchedules = new ConcurrentHashMap<>();

    @Override
    public Feeding save(Feeding feeding) {
        feedingSchedules.put(feeding.getId().getValue(), feeding);
        return feeding;
    }

    @Override
    public Optional<Feeding> findById(FeedingId id) {
        return Optional.ofNullable(feedingSchedules.get(id.getValue()));
    }

    @Override
    public List<Feeding> findAll() {
        return new ArrayList<>(feedingSchedules.values());
    }

    @Override
    public List<Feeding> findByAnimalId(AnimalId animalId) {
        return feedingSchedules.values().stream()
                .filter(schedule -> schedule.getAnimal().getId().equals(animalId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Feeding> findByTimeRange(LocalTime startTime, LocalTime endTime) {
        return feedingSchedules.values().stream()
                .filter(schedule -> {
                    LocalTime scheduleTime = schedule.getFeedingTime().getTime();
                    return !scheduleTime.isBefore(startTime) && !scheduleTime.isAfter(endTime);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Feeding> findByFoodType(FoodType foodType) {
        return feedingSchedules.values().stream()
                .filter(schedule -> schedule.getFoodType() == foodType)
                .collect(Collectors.toList());
    }

    @Override
    public List<Feeding> findPendingFeedings(LocalTime currentTime) {
        return feedingSchedules.values().stream()
                .filter(schedule -> !schedule.isCompleted() &&
                        schedule.isTimeToFeed(currentTime))
                .collect(Collectors.toList());
    }

    @Override
    public List<Feeding> findCompletedFeedingsByDate(LocalDate date) {
        return feedingSchedules.values().stream()
                .filter(schedule -> schedule.isCompleted() &&
                        schedule.getFeedingHistory().contains(date))
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteById(FeedingId id) {
        return feedingSchedules.remove(id.getValue()) != null;
    }

    @Override
    public int deleteByAnimalId(AnimalId animalId) {
        List<String> schedulesToRemove = feedingSchedules.values().stream()
                .filter(schedule -> schedule.getAnimal().getId().equals(animalId))
                .map(schedule -> schedule.getId().getValue())
                .collect(Collectors.toList());

        schedulesToRemove.forEach(feedingSchedules::remove);
        return schedulesToRemove.size();
    }

    @Override
    public void deleteAll() {
        feedingSchedules.clear();
    }

    @Override
    public boolean existsById(FeedingId id) {
        return feedingSchedules.containsKey(id.getValue());
    }

    @Override
    public long count() {
        return feedingSchedules.size();
    }
}
