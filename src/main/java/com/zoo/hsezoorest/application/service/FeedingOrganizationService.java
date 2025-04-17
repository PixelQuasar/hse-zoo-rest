package com.zoo.hsezoorest.application.service;

import com.zoo.hsezoorest.domain.event.FeedingTimeEvent;
import com.zoo.hsezoorest.domain.model.animal.Animal;
import com.zoo.hsezoorest.domain.model.animal.AnimalId;
import com.zoo.hsezoorest.domain.model.feeding.FeedingId;
import com.zoo.hsezoorest.domain.model.feeding.Feeding;
import com.zoo.hsezoorest.domain.model.feeding.FeedingTime;
import com.zoo.hsezoorest.domain.model.feeding.FoodType;
import com.zoo.hsezoorest.domain.repository.AnimalRepository;
import com.zoo.hsezoorest.domain.repository.FeedingRepository;
import com.zoo.hsezoorest.infrastructure.event.EventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
public class FeedingOrganizationService {

    private final AnimalRepository animalRepository;
    private final FeedingRepository feedingScheduleRepository;
    private final EventPublisher eventPublisher;

    public FeedingOrganizationService(AnimalRepository animalRepository,
                                      FeedingRepository feedingScheduleRepository,
                                      EventPublisher eventPublisher) {
        this.animalRepository = animalRepository;
        this.feedingScheduleRepository = feedingScheduleRepository;
        this.eventPublisher = eventPublisher;
    }

    public Feeding createFeedingSchedule(AnimalId animalId, FeedingTime feedingTime, FoodType foodType) {
        log.info("Creating feeding schedule for animal {} at {}", animalId.getValue(), feedingTime.toString());

        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new EntityNotFoundException("Animal not found: " + animalId.getValue()));

        FeedingId feedingId = FeedingId.create();
        Feeding feedingSchedule = new Feeding(feedingId, animal, feedingTime, foodType);

        if (!feedingSchedule.isFoodSuitableForAnimal()) {
            log.warn("Food type {} may not be suitable for {} ({}).",
                    foodType, animal.getName(), animal.getSpecies().getValue());
        }

        return feedingScheduleRepository.save(feedingSchedule);
    }

    public boolean completeFeedingSchedule(FeedingId feedingId) {
        log.info("Marking feeding schedule {} as completed", feedingId.getValue());

        Feeding feedingSchedule = feedingScheduleRepository.findById(feedingId)
                .orElseThrow(() -> new EntityNotFoundException("Feeding schedule not found: " + feedingId.getValue()));

        boolean success = feedingSchedule.markAsCompleted();
        if (success) {
            Animal animal = feedingSchedule.getAnimal();
            animal.feed(feedingSchedule.getFoodType().getDisplayName());

            feedingScheduleRepository.save(feedingSchedule);
            log.info("Feeding completed for animal {}", animal.getName());
        } else {
            log.info("Feeding was already marked as completed");
        }

        return success;
    }

    public List<Feeding> getFeedingSchedulesForAnimal(AnimalId animalId) {
        return feedingScheduleRepository.findByAnimalId(animalId);
    }

    public List<Feeding> getPendingFeedings() {
        return feedingScheduleRepository.findPendingFeedings(LocalTime.now());
    }

    public List<Feeding> getFeedingHistoryByDate(LocalDate date) {
        return feedingScheduleRepository.findCompletedFeedingsByDate(date);
    }

    @Scheduled(cron = "0 * * * * *")
    public void checkFeedingTimes() {
        log.debug("Checking for feeding times...");

        LocalTime currentTime = LocalTime.now();
        List<Feeding> pendingFeedings = feedingScheduleRepository.findPendingFeedings(currentTime);

        for (Feeding schedule : pendingFeedings) {
            FeedingTimeEvent event = getFeedingTimeEvent(schedule);

            eventPublisher.publish(event);
            log.info("Feeding time event published: {}", event.getDescription());
        }
    }

    private static FeedingTimeEvent getFeedingTimeEvent(Feeding schedule) {
        Animal animal = schedule.getAnimal();
        String enclosureId = animal.getCurrentEnclosure() != null ?
                animal.getCurrentEnclosure().getId().getValue() : "Not assigned";

        return new FeedingTimeEvent(
                schedule.getId(),
                animal.getId(),
                animal.getName(),
                animal.getSpecies().getValue(),
                schedule.getFeedingTime().getTime(),
                schedule.getFoodType(),
                enclosureId
        );
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void resetDailyFeedingSchedules() {
        log.info("Resetting feeding schedules for the new day");

        List<Feeding> allSchedules = feedingScheduleRepository.findAll();
        for (Feeding schedule : allSchedules) {
            if (schedule.isCompleted()) {
                schedule.resetCompletionStatus();
                feedingScheduleRepository.save(schedule);
            }
        }
    }
}
