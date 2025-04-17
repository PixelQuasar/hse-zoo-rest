package com.zoo.hsezoorest.application.service;

import com.zoo.hsezoorest.application.dto.statistics.StatisticsDto;
import com.zoo.hsezoorest.domain.model.animal.Animal;
import com.zoo.hsezoorest.domain.model.animal.HealthStatus;
import com.zoo.hsezoorest.domain.model.enclosure.Enclosure;
import com.zoo.hsezoorest.domain.model.feeding.Feeding;
import com.zoo.hsezoorest.domain.repository.AnimalRepository;
import com.zoo.hsezoorest.domain.repository.EnclosureRepository;
import com.zoo.hsezoorest.domain.repository.FeedingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ZooStatisticsService {

    private final AnimalRepository animalRepository;
    private final EnclosureRepository enclosureRepository;
    private final FeedingRepository feedingScheduleRepository;

    public ZooStatisticsService(AnimalRepository animalRepository,
                                EnclosureRepository enclosureRepository,
                                FeedingRepository feedingScheduleRepository) {
        this.animalRepository = animalRepository;
        this.enclosureRepository = enclosureRepository;
        this.feedingScheduleRepository = feedingScheduleRepository;
    }

    public StatisticsDto getZooStatistics() {
        log.info("Generating zoo statistics");

        List<Animal> allAnimals = animalRepository.findAll();
        List<Enclosure> allEnclosures = enclosureRepository.findAll();
        List<Feeding> allFeedingSchedules = feedingScheduleRepository.findAll();

        long totalAnimals = allAnimals.size();
        long healthyAnimals = allAnimals.stream()
                .filter(animal -> animal.getHealthStatus() == HealthStatus.HEALTHY)
                .count();
        long sickAnimals = allAnimals.stream()
                .filter(animal -> animal.getHealthStatus() == HealthStatus.SICK)
                .count();
        long predators = allAnimals.stream()
                .filter(animal -> animal.getSpecies().isPredator())
                .count();
        long herbivores = totalAnimals - predators;

        Map<String, Long> animalsBySpecies = allAnimals.stream()
                .collect(Collectors.groupingBy(
                        animal -> animal.getSpecies().getValue(),
                        Collectors.counting()));

        long totalEnclosures = allEnclosures.size();
        long emptyEnclosures = enclosureRepository.countEmpty();
        Map<String, Long> enclosuresByType = allEnclosures.stream()
                .collect(Collectors.groupingBy(
                        enclosure -> enclosure.getType().getDisplayName(),
                        Collectors.counting()));

        int totalCapacity = allEnclosures.stream()
                .mapToInt(enclosure -> enclosure.getCapacity().getMaximum())
                .sum();
        int usedCapacity = allEnclosures.stream()
                .mapToInt(Enclosure::getCurrentAnimalCount)
                .sum();
        double occupancyRate = totalCapacity > 0 ?
                (double) usedCapacity / totalCapacity * 100 : 0;

        long totalFeedingSchedules = allFeedingSchedules.size();
        long pendingFeedings = feedingScheduleRepository.findPendingFeedings(LocalTime.now()).size();
        long completedFeedingsToday = feedingScheduleRepository.findCompletedFeedingsByDate(LocalDate.now()).size();
        Map<String, Long> feedingsByFoodType = allFeedingSchedules.stream()
                .collect(Collectors.groupingBy(
                        schedule -> schedule.getFoodType().getDisplayName(),
                        Collectors.counting()));

        return StatisticsDto.builder()
                .totalAnimals(totalAnimals)
                .totalEnclosures(totalEnclosures)
                .emptyEnclosures(emptyEnclosures)
                .totalFeedingSchedules(totalFeedingSchedules)
                .healthyAnimals(healthyAnimals)
                .sickAnimals(sickAnimals)
                .predators(predators)
                .herbivores(herbivores)
                .animalsBySpecies(animalsBySpecies)
                .enclosuresByType(enclosuresByType)
                .totalCapacity(totalCapacity)
                .usedCapacity(usedCapacity)
                .occupancyRate(occupancyRate)
                .pendingFeedings(pendingFeedings)
                .completedFeedings(completedFeedingsToday)
                .feedingsByFoodType(feedingsByFoodType)
                .build();
    }

    public long getTotalAnimalCount() {
        return animalRepository.count();
    }

    public long getTotalEnclosureCount() {
        return enclosureRepository.count();
    }

    public long getEmptyEnclosureCount() {
        return enclosureRepository.countEmpty();
    }

    public double getOccupancyRate() {
        List<Enclosure> allEnclosures = enclosureRepository.findAll();

        int totalCapacity = allEnclosures.stream()
                .mapToInt(enclosure -> enclosure.getCapacity().getMaximum())
                .sum();

        int usedCapacity = allEnclosures.stream()
                .mapToInt(Enclosure::getCurrentAnimalCount)
                .sum();

        return totalCapacity > 0 ? (double) usedCapacity / totalCapacity * 100 : 0;
    }

    public long getSickAnimalCount() {
        return animalRepository.findSickAnimals().size();
    }

    public long getPendingFeedingCount() {
        return feedingScheduleRepository.findPendingFeedings(LocalTime.now()).size();
    }
}
