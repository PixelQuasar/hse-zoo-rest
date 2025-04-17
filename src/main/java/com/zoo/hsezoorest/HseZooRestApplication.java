package com.zoo.hsezoorest;

import com.zoo.hsezoorest.domain.model.animal.*;
import com.zoo.hsezoorest.domain.model.enclosure.*;
import com.zoo.hsezoorest.domain.model.feeding.*;
import com.zoo.hsezoorest.domain.repository.AnimalRepository;
import com.zoo.hsezoorest.domain.repository.EnclosureRepository;
import com.zoo.hsezoorest.domain.repository.FeedingRepository;
import com.zoo.hsezoorest.application.service.AnimalTransferService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDate;
import java.time.LocalTime;

@Slf4j
@SpringBootApplication
@EnableScheduling
@OpenAPIDefinition(
        info = @Info(
                title = "HSE Zoo Management System",
                version = "1.0.0",
                description = "REST API for managing zoo animals, enclosures and feeding schedules",
                license = @License(name = "HSE License", url = "https://www.hse.ru"),
                contact = @Contact(name = "HSE Zoo Management", email = "zoo@hse.ru")
        )
)
public class HseZooRestApplication {

    public static void main(String[] args) {
        SpringApplication.run(HseZooRestApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(
            AnimalRepository animalRepository,
            EnclosureRepository enclosureRepository,
            FeedingRepository feedingRepository,
            AnimalTransferService animalTransferService) {

        return args -> {
            log.info("Initializing zoo with sample data...");

            // Create enclosures
            Enclosure predatorEnclosure = new Enclosure(
                    EnclosureId.create(),
                    EnclosureType.PREDATOR,
                    Capacity.of(5)
            );

            Enclosure herbivoreEnclosure = new Enclosure(
                    EnclosureId.create(),
                    EnclosureType.HERBIVORE,
                    Capacity.of(8)
            );

            Enclosure aviaryEnclosure = new Enclosure(
                    EnclosureId.create(),
                    EnclosureType.AVIARY,
                    Capacity.of(12)
            );

            Enclosure aquariumEnclosure = new Enclosure(
                    EnclosureId.create(),
                    EnclosureType.AQUARIUM,
                    Capacity.of(20)
            );

            enclosureRepository.save(predatorEnclosure);
            enclosureRepository.save(herbivoreEnclosure);
            enclosureRepository.save(aviaryEnclosure);
            enclosureRepository.save(aquariumEnclosure);

            log.info("Created enclosures: predator, herbivore, aviary, and aquarium");

            // Create animals
            Animal lion = new Animal(
                    AnimalId.create(),
                    Species.predator("Lion"),
                    "Simba",
                    LocalDate.of(2018, 5, 15),
                    Gender.MALE,
                    FavoriteFood.of("Meat")
            );

            Animal tiger = new Animal(
                    AnimalId.create(),
                    Species.predator("Tiger"),
                    "Raja",
                    LocalDate.of(2019, 3, 10),
                    Gender.FEMALE,
                    FavoriteFood.of("Meat")
            );

            Animal elephant = new Animal(
                    AnimalId.create(),
                    Species.herbivore("Elephant"),
                    "Dumbo",
                    LocalDate.of(2015, 9, 22),
                    Gender.MALE,
                    FavoriteFood.of("Fruits")
            );

            Animal giraffe = new Animal(
                    AnimalId.create(),
                    Species.herbivore("Giraffe"),
                    "Melman",
                    LocalDate.of(2017, 7, 8),
                    Gender.MALE,
                    FavoriteFood.of("Leaves")
            );

            Animal parrot = new Animal(
                    AnimalId.create(),
                    Species.herbivore("Parrot"),
                    "Rio",
                    LocalDate.of(2020, 2, 14),
                    Gender.MALE,
                    FavoriteFood.of("Seeds")
            );

            animalRepository.save(lion);
            animalRepository.save(tiger);
            animalRepository.save(elephant);
            animalRepository.save(giraffe);
            animalRepository.save(parrot);

            log.info("Created animals: lion, tiger, elephant, giraffe, and parrot");

            animalTransferService.transferAnimal(lion.getId(), predatorEnclosure.getId(), "Initial assignment");
            animalTransferService.transferAnimal(tiger.getId(), predatorEnclosure.getId(), "Initial assignment");
            animalTransferService.transferAnimal(elephant.getId(), herbivoreEnclosure.getId(), "Initial assignment");
            animalTransferService.transferAnimal(giraffe.getId(), herbivoreEnclosure.getId(), "Initial assignment");
            animalTransferService.transferAnimal(parrot.getId(), aviaryEnclosure.getId(), "Initial assignment");

            log.info("Assigned animals to appropriate enclosures");

            Feeding lionFeeding = new Feeding(
                    FeedingId.create(),
                    lion,
                    FeedingTime.of(LocalTime.of(10, 0)),
                    FoodType.MEAT
            );

            Feeding tigerFeeding = new Feeding(
                    FeedingId.create(),
                    tiger,
                    FeedingTime.of(LocalTime.of(10, 30)),
                    FoodType.MEAT
            );

            Feeding elephantFeeding = new Feeding(
                    FeedingId.create(),
                    elephant,
                    FeedingTime.of(LocalTime.of(9, 0)),
                    FoodType.FRUITS
            );

            Feeding giraffeFeeding = new Feeding(
                    FeedingId.create(),
                    giraffe,
                    FeedingTime.of(LocalTime.of(11, 0)),
                    FoodType.VEGETABLES
            );

            Feeding parrotFeeding = new Feeding(
                    FeedingId.create(),
                    parrot,
                    FeedingTime.of(LocalTime.of(8, 0)),
                    FoodType.SEEDS
            );

            feedingRepository.save(lionFeeding);
            feedingRepository.save(tigerFeeding);
            feedingRepository.save(elephantFeeding);
            feedingRepository.save(giraffeFeeding);
            feedingRepository.save(parrotFeeding);

            log.info("Created feeding schedules for all animals");

            tiger.markAsSick();
            animalRepository.save(tiger);

            log.info("Sample data initialization completed successfully!");
        };
    }
}
