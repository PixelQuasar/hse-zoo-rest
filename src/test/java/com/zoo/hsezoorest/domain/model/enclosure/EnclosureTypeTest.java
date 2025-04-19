package com.zoo.hsezoorest.domain.model.enclosure;

import com.zoo.hsezoorest.domain.model.animal.Animal;
import com.zoo.hsezoorest.domain.model.animal.Species;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class EnclosureTypeTest {

    @Mock
    private Animal mockAnimal;
    @Mock
    private Species mockSpecies;

    private void setupSpeciesMock(boolean isPredator, boolean isAvian) {
        lenient().when(mockAnimal.getSpecies()).thenReturn(mockSpecies);
        lenient().when(mockSpecies.isPredator()).thenReturn(isPredator);
        lenient().when(mockSpecies.isAvian()).thenReturn(isAvian);
    }

    // --- PREDATOR Enclosure Tests ---
    @Test
    void canHouseAnimal_PredatorEnclosure_shouldHousePredator() {
        setupSpeciesMock(true, false);
        assertTrue(EnclosureType.PREDATOR.canHouseAnimal(mockAnimal));
    }

    @Test
    void canHouseAnimal_PredatorEnclosure_shouldNotHouseHerbivore() {
        setupSpeciesMock(false, false);
        assertFalse(EnclosureType.PREDATOR.canHouseAnimal(mockAnimal));
    }

    @Test
    void canHouseAnimal_PredatorEnclosure_shouldNotHouseAvianPredator() {
        setupSpeciesMock(true, true); // Predator, but also Avian
        assertFalse(EnclosureType.PREDATOR.canHouseAnimal(mockAnimal));
    }

    // --- HERBIVORE Enclosure Tests ---
    @Test
    void canHouseAnimal_HerbivoreEnclosure_shouldHouseHerbivore() {
        setupSpeciesMock(false, false);
        assertTrue(EnclosureType.HERBIVORE.canHouseAnimal(mockAnimal));
    }

    @Test
    void canHouseAnimal_HerbivoreEnclosure_shouldNotHousePredator() {
        setupSpeciesMock(true, false);
        assertFalse(EnclosureType.HERBIVORE.canHouseAnimal(mockAnimal));
    }

    @Test
    void canHouseAnimal_HerbivoreEnclosure_shouldNotHouseAvianHerbivore() {
        setupSpeciesMock(false, true); // Herbivore, but also Avian
        assertFalse(EnclosureType.HERBIVORE.canHouseAnimal(mockAnimal));
    }

    // --- AVIARY Enclosure Tests ---
    @Test
    void canHouseAnimal_AviaryEnclosure_shouldHouseAvianHerbivore() {
        setupSpeciesMock(false, true);
        assertTrue(EnclosureType.AVIARY.canHouseAnimal(mockAnimal));
    }

    @Test
    void canHouseAnimal_AviaryEnclosure_shouldNotHouseNonAvianHerbivore() {
        setupSpeciesMock(false, false);
        assertFalse(EnclosureType.AVIARY.canHouseAnimal(mockAnimal));
    }

    @Test
    void canHouseAnimal_AviaryEnclosure_shouldNotHousePredator() {
        setupSpeciesMock(true, false);
        assertFalse(EnclosureType.AVIARY.canHouseAnimal(mockAnimal));
    }

    @Test
    void canHouseAnimal_AviaryEnclosure_shouldNotHouseAvianPredator() {
        // Aviary only houses non-predator birds according to its definition
        setupSpeciesMock(true, true);
        assertFalse(EnclosureType.AVIARY.canHouseAnimal(mockAnimal));
    }

    // --- MIXED Enclosure Tests ---
    @Test
    void canHouseAnimal_MixedEnclosure_shouldHouseAnyAnimal() {
        setupSpeciesMock(true, false); // Predator
        assertTrue(EnclosureType.MIXED.canHouseAnimal(mockAnimal));

        setupSpeciesMock(false, false); // Herbivore
        assertTrue(EnclosureType.MIXED.canHouseAnimal(mockAnimal));

        setupSpeciesMock(false, true); // Avian Herbivore
        assertTrue(EnclosureType.MIXED.canHouseAnimal(mockAnimal));

        setupSpeciesMock(true, true); // Avian Predator
        assertTrue(EnclosureType.MIXED.canHouseAnimal(mockAnimal));
    }

    // --- AQUARIUM/TERRARIUM Enclosure Tests (Assuming they don't house predators/herbivores/birds based on constructor) ---
    @Test
    void canHouseAnimal_AquariumTerrarium_shouldNotHouseLandAnimalsOrBirds() {
        EnclosureType[] types = {EnclosureType.AQUARIUM, EnclosureType.TERRARIUM};
        for (EnclosureType type : types) {
            setupSpeciesMock(true, false); // Predator
            assertFalse(type.canHouseAnimal(mockAnimal), type + " failed for Predator");

            setupSpeciesMock(false, false); // Herbivore
            assertFalse(type.canHouseAnimal(mockAnimal), type + " failed for Herbivore");

            setupSpeciesMock(false, true); // Avian Herbivore
            assertFalse(type.canHouseAnimal(mockAnimal), type + " failed for Avian Herbivore");

            setupSpeciesMock(true, true); // Avian Predator
            assertFalse(type.canHouseAnimal(mockAnimal), type + " failed for Avian Predator");
        }
    }

    // --- Display Name Test ---
    @Test
    void getDisplayName_shouldReturnCorrectName() {
        assertEquals("Predator", EnclosureType.PREDATOR.getDisplayName());
        assertEquals("Herbivore", EnclosureType.HERBIVORE.getDisplayName());
        assertEquals("Aviary", EnclosureType.AVIARY.getDisplayName());
        assertEquals("Aquarium", EnclosureType.AQUARIUM.getDisplayName());
        assertEquals("Terrarium", EnclosureType.TERRARIUM.getDisplayName());
        assertEquals("Mixed", EnclosureType.MIXED.getDisplayName());
    }
} 