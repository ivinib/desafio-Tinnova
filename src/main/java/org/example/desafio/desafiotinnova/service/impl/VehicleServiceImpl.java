package org.example.desafio.desafiotinnova.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.desafio.desafiotinnova.dto.request.VehicleCreateDTO;
import org.example.desafio.desafiotinnova.dto.request.VehicleUpdateDTO;
import org.example.desafio.desafiotinnova.dto.response.ReportBrandDTO;
import org.example.desafio.desafiotinnova.dto.response.VehicleResponseDTO;
import org.example.desafio.desafiotinnova.exception.LicencePlateDuplicated;
import org.example.desafio.desafiotinnova.exception.ResourceNotFoundException;
import org.example.desafio.desafiotinnova.model.Vehicle;
import org.example.desafio.desafiotinnova.repository.VehicleRepository;
import org.example.desafio.desafiotinnova.repository.VehicleSpecifications;
import org.example.desafio.desafiotinnova.service.contract.CurrencyService;
import org.example.desafio.desafiotinnova.service.contract.VehicleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final CurrencyService currencyService;

    @Override
    @Transactional
    public VehicleResponseDTO create(VehicleCreateDTO dto) {
        if (vehicleRepository.existsVehicleByLicencePlate(dto.licencePlate())) {
            throw new LicencePlateDuplicated("Vehicle with licence plate: " + dto.licencePlate() + " is already registered");
        }
        BigDecimal dollarRate = currencyService.getUSDDollarRate();
        BigDecimal priceUSD = dto.price().divide(dollarRate, 4, RoundingMode.HALF_UP);

        Vehicle vehicle = new Vehicle();
        vehicle.setLicencePlate(dto.licencePlate());
        vehicle.setBrand(dto.brand());
        vehicle.setYear(dto.year());
        vehicle.setColor(dto.color());
        vehicle.setPrice(priceUSD);
        vehicle.setActive(true);

        log.info("Creating vehicle with licence plate: {}", dto.licencePlate());

        return mapToResponse(vehicleRepository.save(vehicle), dollarRate);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VehicleResponseDTO> listWithFilter(String brand, Integer year, String color, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        Specification<Vehicle> spec = VehicleSpecifications.byFilters(brand, year, color, minPrice, maxPrice)
                .and((root, query, cb) -> cb.equal(root.get("active"), true));
        BigDecimal dollarRate = currencyService.getUSDDollarRate();
        log.info("Listing vehicles with filters");
        return vehicleRepository.findAll(spec, pageable).map(v -> mapToResponse(v, dollarRate));
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleResponseDTO findById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with Id: " + id));

        if (!vehicle.isActive()) {
            throw new ResourceNotFoundException("Vehicle not found with Id: " + id);
        }
        
        return mapToResponse(vehicle, currencyService.getUSDDollarRate());
    }

    @Override
    @Transactional
    public VehicleResponseDTO updateTotal(Long id, VehicleUpdateDTO dto) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with Id: " + id));

        if (!vehicle.isActive()) {
            throw new ResourceNotFoundException("Vehicle not found with Id: " + id);
        }

        validarNovaPlaca(id, dto.licencePlate());

        vehicle.setLicencePlate(dto.licencePlate());
        vehicle.setBrand(dto.brand());
        vehicle.setYear(dto.year());
        vehicle.setColor(dto.color());

        BigDecimal dollarRate = currencyService.getUSDDollarRate();
        if (dto.priceBRL() != null) {
            vehicle.setPrice(dto.priceBRL().divide(dollarRate, 4, RoundingMode.HALF_UP));
        }

        return mapToResponse(vehicleRepository.save(vehicle), dollarRate);
    }

    @Override
    @Transactional
    public VehicleResponseDTO updateParcial(Long id, VehicleUpdateDTO dto) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with Id: " + id));

        if (!vehicle.isActive()) {
            throw new ResourceNotFoundException("Vehicle not found with Id: " + id);
        }

        if (dto.licencePlate() != null) {
            validarNovaPlaca(id, dto.licencePlate());
            vehicle.setLicencePlate(dto.licencePlate());
        }

        if (dto.brand() != null) vehicle.setBrand(dto.brand());
        if (dto.year() != null) vehicle.setYear(dto.year());
        if (dto.color() != null) vehicle.setColor(dto.color());

        BigDecimal dollarRate = currencyService.getUSDDollarRate();
        if (dto.priceBRL() != null) {
            vehicle.setPrice(dto.priceBRL().divide(dollarRate, 4, RoundingMode.HALF_UP));
        }

        return mapToResponse(vehicleRepository.save(vehicle), dollarRate);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Optional<Vehicle> vehicle = vehicleRepository.findById(id);
        if (vehicle.isEmpty()) {
            throw new ResourceNotFoundException("Vehicle not found with Id: " + id);
        }

        if (vehicle.get().isActive()) {
            throw new IllegalStateException("Active vehicle cannot be deleted: " + id);
        }
        
        vehicleRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportBrandDTO> getReportByBrand() {
        return vehicleRepository.getReportByBrand();
    }


    private void validarNovaPlaca(Long currentId, String newLicencePlate) {
        if (vehicleRepository.existsByLicencePlateAndIdVehicleNot(newLicencePlate, currentId)) {
            throw new LicencePlateDuplicated("Vehicle with licence plate: " + newLicencePlate + " is already registered by another vehicle");
        }
    }

    private VehicleResponseDTO mapToResponse(Vehicle vehicle, BigDecimal dollarRate) {
        BigDecimal priceBRL = vehicle.getPrice().multiply(dollarRate).setScale(2, RoundingMode.HALF_UP);
        return new VehicleResponseDTO(
                vehicle.getIdVehicle(), vehicle.getLicencePlate(), vehicle.getBrand(), vehicle.getYear(), vehicle.getColor(),
                vehicle.getPrice().setScale(2, RoundingMode.HALF_UP), priceBRL, dollarRate, vehicle.isActive()
        );
    }
}
