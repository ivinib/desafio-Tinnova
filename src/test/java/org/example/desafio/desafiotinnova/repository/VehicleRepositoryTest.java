package org.example.desafio.desafiotinnova.repository;

import org.example.desafio.desafiotinnova.model.Vehicle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
public class VehicleRepositoryTest {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Test
    @DisplayName("Should throw an exeption when trying to register a duplicated licence plate")
    void testShouldThrowExceptionWithDuplicatedLicencePlateRegistered() {

        Vehicle v1 = new Vehicle(null, "ABC1D23", "Ford", 2023, "Preto", BigDecimal.valueOf(10000), true);
        vehicleRepository.saveAndFlush(v1);

        Vehicle v2 = new Vehicle(null, "ABC1D23", "Chevrolet", 2024, "Branco", BigDecimal.valueOf(12000), true);

        assertThrows(DataIntegrityViolationException.class, () -> {
            vehicleRepository.saveAndFlush(v2);
        });
    }

    @Test
    @DisplayName("Should throw an exception when the same licence plate is registered in another vehicle")
    void testShouldThrowExceptionWhenLicencePlateIsRegisteredInAnotherVehicle() {
        Vehicle v1 = vehicleRepository.save(new Vehicle(null, "ABC1D23", "Ford", 2023, "Preto", BigDecimal.valueOf(10000), true));
        vehicleRepository.save(new Vehicle(null, "XYZ9E87", "Fiat", 2022, "Azul", BigDecimal.valueOf(8000), true));


        boolean existent = vehicleRepository.existsByLicencePlateAndIdVehicleNot("XYZ9E87", v1.getIdVehicle());
        assertThat(existent).isTrue();


        boolean sameVehicle = vehicleRepository.existsByLicencePlateAndIdVehicleNot("ABC1D23", v1.getIdVehicle());
        assertThat(sameVehicle).isFalse();
    }
}
