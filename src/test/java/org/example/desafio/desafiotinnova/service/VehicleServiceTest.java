package org.example.desafio.desafiotinnova.service;

import org.example.desafio.desafiotinnova.dto.request.VehicleCreateDTO;
import org.example.desafio.desafiotinnova.dto.request.VehicleUpdateDTO;
import org.example.desafio.desafiotinnova.exception.LicencePlateDuplicated;
import org.example.desafio.desafiotinnova.exception.ResourceNotFoundException;
import org.example.desafio.desafiotinnova.model.Vehicle;
import org.example.desafio.desafiotinnova.repository.VehicleRepository;
import org.example.desafio.desafiotinnova.service.contract.CurrencyService;
import org.example.desafio.desafiotinnova.service.impl.VehicleServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VehicleServiceTest {
    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private CurrencyService currencyService;

    @InjectMocks
    private VehicleServiceImpl vehicleService;

    @Test
    @DisplayName("Validate licence plate duplicated")
    void testValidateLicenceDuplicated() {
        VehicleCreateDTO dto = new VehicleCreateDTO("ABC1D23", "Ford", 2023, "Preto", BigDecimal.valueOf(50000));
        when(vehicleRepository.existsVehicleByLicencePlate("ABC1D23")).thenReturn(true);

        assertThrows(LicencePlateDuplicated.class, () -> vehicleService.create(dto));
        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }

    @Test
    @DisplayName("Update operation invalid if id does not exist")
    void testInvalidUpdateOperationWithoutIdValue() {
        Long idInexistent = 99L;
        VehicleUpdateDTO dto = new VehicleUpdateDTO("ABC1D23", "Ford", 2023, "Preto", BigDecimal.valueOf(60000));
        when(vehicleRepository.findById(idInexistent)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> vehicleService.updateTotal(idInexistent, dto));
        assertThrows(ResourceNotFoundException.class, () -> vehicleService.updateParcial(idInexistent, dto));
    }

    @Test
    @DisplayName("Validate filtering(Specification)")
    void testFilteringOptions() {
        Pageable pageable = PageRequest.of(0, 10);
        when(currencyService.getUSDDollarRate()).thenReturn(BigDecimal.valueOf(5.00));

        Vehicle vehicle = new Vehicle(1L, "ABC1D23", "Ford", 2023, "Preto", BigDecimal.valueOf(10000), true);
        Page<Vehicle> pageFake = new PageImpl<>(List.of(vehicle));

        when(vehicleRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(pageFake);

        var result = vehicleService.listWithFilter("Ford", 2023, "Preto", null, null, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
    }
}
