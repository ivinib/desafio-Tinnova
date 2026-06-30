package org.example.desafio.desafiotinnova.service;

import org.example.desafio.desafiotinnova.dto.request.VehicleCreateDTO;
import org.example.desafio.desafiotinnova.dto.request.VehicleUpdateDTO;
import org.example.desafio.desafiotinnova.dto.response.ReportBrandDTO;
import org.example.desafio.desafiotinnova.dto.response.VehicleResponseDTO;
import org.example.desafio.desafiotinnova.exception.LicensePlateDuplicated;
import org.example.desafio.desafiotinnova.exception.ResourceNotFoundException;
import org.example.desafio.desafiotinnova.model.Vehicle;
import org.example.desafio.desafiotinnova.repository.VehicleRepository;
import org.example.desafio.desafiotinnova.service.contract.CurrencyService;
import org.example.desafio.desafiotinnova.service.impl.VehicleServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

    @Nested
    @DisplayName("Create Vehicle")
    class CreateTests {

        @Test
        @DisplayName("Create vehicle and convert price")
        void testCreateVehicle() {
            VehicleCreateDTO dto = new VehicleCreateDTO("ABC1D23", "Ford", 2023, "Preto", BigDecimal.valueOf(50000.00));
            BigDecimal dollarRate = BigDecimal.valueOf(5.00);

            Vehicle savedVehicle = new Vehicle(1L, "ABC1D23", "Ford", 2023, "Preto", BigDecimal.valueOf(10000.00), true);

            when(vehicleRepository.existsVehicleByLicensePlate("ABC1D23")).thenReturn(false);
            when(currencyService.getUSDDollarRate()).thenReturn(dollarRate);
            when(vehicleRepository.save(any(Vehicle.class))).thenReturn(savedVehicle);

            VehicleResponseDTO response = vehicleService.create(dto);

            assertThat(response).isNotNull();
            assertThat(response.idVehicle()).isEqualTo(1L);
            assertThat(response.priceInUSD()).isEqualByComparingTo("10000.00");
            assertThat(response.priceInBRL()).isEqualByComparingTo("50000.00");
            verify(vehicleRepository, times(1)).save(any(Vehicle.class));
        }

        @Test
        @DisplayName("Should throw an exception for license duplicated")
        void testCreateVehicleLicenseDuplicated() {
            VehicleCreateDTO dto = new VehicleCreateDTO("ABC1D23", "Ford", 2023, "Preto", BigDecimal.valueOf(50000.00));
            when(vehicleRepository.existsVehicleByLicensePlate("ABC1D23")).thenReturn(true);

            assertThrows(LicensePlateDuplicated.class, () -> vehicleService.create(dto));
            verify(vehicleRepository, never()).save(any(Vehicle.class));
        }
    }

    @Nested
    @DisplayName("Filtered list of vehicles")
    class ListWithFilterTests {

        @Test
        @DisplayName("List vehicles with filters")
        void testListWithFilter() {
            Pageable pageable = PageRequest.of(0, 10);
            BigDecimal dollarRate = BigDecimal.valueOf(5.00);
            Vehicle vehicle = new Vehicle(1L, "ABC1D23", "Ford", 2023, "Preto", BigDecimal.valueOf(10000.00), true);
            Page<Vehicle> mockPage = new PageImpl<>(List.of(vehicle));

            when(currencyService.getUSDDollarRate()).thenReturn(dollarRate);
            when(vehicleRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(mockPage);

            Page<VehicleResponseDTO> result = vehicleService.listWithFilter("Ford", 2023, "Preto", null, null, pageable);

            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).licensePlate()).isEqualTo("ABC1D23");
        }
    }

    @Nested
    @DisplayName("Get vehicle by ID")
    class FindByIdTests {

        @Test
        @DisplayName("Should get an active vehicle by it ID")
        void testFindById() {
            Vehicle vehicle = new Vehicle(1L, "ABC1D23", "Ford", 2023, "Preto", BigDecimal.valueOf(10000.00), true);
            when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
            when(currencyService.getUSDDollarRate()).thenReturn(BigDecimal.valueOf(5.00));

            VehicleResponseDTO response = vehicleService.findById(1L);

            assertThat(response).isNotNull();
            assertThat(response.idVehicle()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Should throw an exception if does not find vehicle by ID")
        void testFindByIdNotFound() {
            when(vehicleRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> vehicleService.findById(1L));
        }

        @Test
        @DisplayName("Should throw an exception if the vehicle is inactive")
        void testFindByIdInactive() {
            Vehicle vehicle = new Vehicle(1L, "ABC1D23", "Ford", 2023, "Preto", BigDecimal.valueOf(10000.00), false); // active = false
            when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));

            assertThrows(ResourceNotFoundException.class, () -> vehicleService.findById(1L));
        }
    }

    @Nested
    @DisplayName("Update vehicle")
    class UpdateTotalTests {

        @Test
        @DisplayName("Should update the vehicle")
        void testUpdateTotal() {
            Vehicle vehicle = new Vehicle(1L, "ABC1D23", "Ford", 2023, "Preto", BigDecimal.valueOf(10000.00), true);
            VehicleUpdateDTO dto = new VehicleUpdateDTO("XYZ9E87", "Chevrolet", 2024, "Branco", BigDecimal.valueOf(75000.00));

            when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
            when(vehicleRepository.existsByLicensePlateAndIdVehicleNot("XYZ9E87", 1L)).thenReturn(false);
            when(currencyService.getUSDDollarRate()).thenReturn(BigDecimal.valueOf(5.00));
            when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(invocation -> invocation.getArgument(0));

            VehicleResponseDTO response = vehicleService.updateTotal(1L, dto);

            assertThat(response.licensePlate()).isEqualTo("XYZ9E87");
            assertThat(response.brand()).isEqualTo("Chevrolet");
            assertThat(response.priceInUSD()).isEqualByComparingTo("15000.00");
        }

        @Test
        @DisplayName("Should throw exception if the license plate already is registered in another vehicle")
        void testUpdateTotallicensePlateConflict() {
            Vehicle vehicle = new Vehicle(1L, "ABC1D23", "Ford", 2023, "Preto", BigDecimal.valueOf(10000.00), true);
            VehicleUpdateDTO dto = new VehicleUpdateDTO("CONFLITO1", "Chevrolet", 2024, "Branco", BigDecimal.valueOf(75000.00));

            when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
            when(vehicleRepository.existsByLicensePlateAndIdVehicleNot("CONFLITO1", 1L)).thenReturn(true);

            assertThrows(LicensePlateDuplicated.class, () -> vehicleService.updateTotal(1L, dto));
        }
    }
    @Nested
    @DisplayName("Partial update vehicle")
    class UpdateParcialTests {

        @Test
        @DisplayName("Should update only the fields sent in the DTO")
        void testUpdateParcial() {
            Vehicle vehicle = new Vehicle(1L, "ABC1D23", "Ford", 2023, "Preto", BigDecimal.valueOf(10000.00), true);
            VehicleUpdateDTO dto = new VehicleUpdateDTO(null, null, null, "Vermelho", null); // Apenas cor muda

            when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
            when(currencyService.getUSDDollarRate()).thenReturn(BigDecimal.valueOf(5.00));
            when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(invocation -> invocation.getArgument(0));

            VehicleResponseDTO response = vehicleService.updateParcial(1L, dto);

            assertThat(response.color()).isEqualTo("Vermelho");
            assertThat(response.brand()).isEqualTo("Ford"); // Manteve o original
            assertThat(response.licensePlate()).isEqualTo("ABC1D23"); // Manteve o original
        }
    }

    @Nested
    @DisplayName("Deletion tests")
    class DeleteTests {

        @Test
        @DisplayName("Delete successfully if is not active")
        void testDelete() {
            Vehicle vehicle = new Vehicle(1L, "ABC1D23", "Ford", 2023, "Preto", BigDecimal.valueOf(10000.00), false);
            when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));

            vehicleService.delete(1L);

            verify(vehicleRepository, times(1)).deleteById(1L);
        }
    }
    @Nested
    @DisplayName("Report by brand")
    class ReportTests {
        @Test
        @DisplayName("Should return the report list provided by the repository")
        void testGetReportByBrand() {
            List<ReportBrandDTO> mockReport = List.of(new ReportBrandDTO("Ford", 1L, 50000.00));
            when(vehicleRepository.getReportByBrand()).thenReturn(mockReport);

            List<ReportBrandDTO> response = vehicleService.getReportByBrand();

            assertThat(response).isNotEmpty().hasSize(1);
            assertThat(response.get(0).brand()).isEqualTo("Ford");
        }
    }
}
