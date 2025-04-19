package com.zoo.hsezoorest.domain.model.animal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.zoo.hsezoorest.domain.model.enclosure.Enclosure;
import com.zoo.hsezoorest.domain.model.enclosure.EnclosureType;

@ExtendWith(MockitoExtension.class)
class AnimalTest {

    private AnimalId animalId;
    private Species species;
    private String name;
    private LocalDate birthDate;
    private Gender gender;
    private FavoriteFood favoriteFood;
    private Animal animal;

    @Mock
    private Enclosure mockEnclosure1;
    @Mock
    private Enclosure mockEnclosure2;

    @BeforeEach
    void setUp() {
        animalId = AnimalId.create();
        species = Species.predator("Lion");
        name = "Simba";
        birthDate = LocalDate.of(2020, 1, 1);
        gender = Gender.MALE;
        favoriteFood = FavoriteFood.of("Meat");
        animal = new Animal(animalId, species, name, birthDate, gender, favoriteFood);
    }

    @Test
    void constructor_shouldInitializeAnimalCorrectly() {
        assertEquals(animalId, animal.getId());
        assertEquals(species, animal.getSpecies());
        assertEquals(name, animal.getName());
        assertEquals(birthDate, animal.getBirthDate());
        assertEquals(gender, animal.getGender());
        assertEquals(favoriteFood, animal.getFavoriteFood());
        assertEquals(HealthStatus.HEALTHY, animal.getHealthStatus()); // Initial status should be Healthy
        assertNull(animal.getCurrentEnclosure()); // Initial enclosure should be null
    }

    @Test
    void feed_shouldReturnTrue_whenFoodIsFavorite() {
        assertTrue(animal.feed("Meat"));
    }

    @Test
    void feed_shouldReturnFalse_whenFoodIsNotFavorite() {
        assertFalse(animal.feed("Grass"));
    }

    @Test
    void heal_shouldReturnTrueAndSetStatusToHealthy_whenAnimalIsSick() {
        animal.markAsSick(); // Make animal sick first
        assertTrue(animal.heal());
        assertEquals(HealthStatus.HEALTHY, animal.getHealthStatus());
    }

    @Test
    void heal_shouldReturnFalse_whenAnimalIsAlreadyHealthy() {
        assertEquals(HealthStatus.HEALTHY, animal.getHealthStatus()); // Ensure animal is healthy
        assertFalse(animal.heal());
        assertEquals(HealthStatus.HEALTHY, animal.getHealthStatus()); // Status should remain Healthy
    }

    @Test
    void markAsSick_shouldSetStatusToSick() {
        animal.markAsSick();
        assertEquals(HealthStatus.SICK, animal.getHealthStatus());
    }

    @Test
    void changeFavoriteFood_shouldUpdateFavoriteFood() {
        FavoriteFood newFood = FavoriteFood.of("Fish");
        animal.changeFavoriteFood(newFood);
        assertEquals(newFood, animal.getFavoriteFood());
        assertTrue(animal.feed("Fish"));
        assertFalse(animal.feed("Meat"));
    }

    @Test
    void moveToEnclosure_shouldMoveAnimal_whenEnclosureCanHouseAnimal() {
        when(mockEnclosure1.canHouseAnimal(animal)).thenReturn(true);
        doNothing().when(mockEnclosure1).addAnimal(animal);

        animal.moveToEnclosure(mockEnclosure1);

        assertEquals(mockEnclosure1, animal.getCurrentEnclosure());
        verify(mockEnclosure1).canHouseAnimal(animal);
        verify(mockEnclosure1).addAnimal(animal);
        verify(mockEnclosure2, never()).removeAnimal(any());
    }

    @Test
    void moveToEnclosure_shouldThrowException_whenEnclosureCannotHouseAnimal() {
        when(mockEnclosure1.canHouseAnimal(animal)).thenReturn(false);
        when(mockEnclosure1.getType()).thenReturn(EnclosureType.AVIARY);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            animal.moveToEnclosure(mockEnclosure1);
        });
        assertTrue(exception.getMessage().contains("Cannot move Lion to enclosure of type AVIARY"));
        assertNull(animal.getCurrentEnclosure());
        verify(mockEnclosure1).canHouseAnimal(animal);
        verify(mockEnclosure1, never()).addAnimal(any());
    }

    @Test
    void moveToEnclosure_shouldRemoveAnimalFromOldEnclosure() {
        when(mockEnclosure1.canHouseAnimal(animal)).thenReturn(true);
        doNothing().when(mockEnclosure1).addAnimal(animal);
        animal.moveToEnclosure(mockEnclosure1);
        assertEquals(mockEnclosure1, animal.getCurrentEnclosure());

        when(mockEnclosure2.canHouseAnimal(animal)).thenReturn(true);
        doNothing().when(mockEnclosure2).addAnimal(animal);
        doNothing().when(mockEnclosure1).removeAnimal(animal);

        animal.moveToEnclosure(mockEnclosure2);

        assertEquals(mockEnclosure2, animal.getCurrentEnclosure());
        verify(mockEnclosure1).removeAnimal(animal);
        verify(mockEnclosure2).addAnimal(animal);
    }
} 