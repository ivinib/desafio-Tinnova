package org.example.desafio.desafiotinnova.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.desafio.desafiotinnova.dto.request.VehicleCreateDTO;
import org.example.desafio.desafiotinnova.dto.request.VehicleUpdateDTO;
import org.example.desafio.desafiotinnova.dto.response.ReportBrandDTO;
import org.example.desafio.desafiotinnova.dto.response.VehicleResponseDTO;
import org.example.desafio.desafiotinnova.exception.LicensePlateDuplicated;
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

    //Implementation of business rules of creating the vehicle and saving to database
    @Override
    @Transactional
    public VehicleResponseDTO create(VehicleCreateDTO dto) {
        //Assures the license plate to be created in uppercase
        String toUpperCaseLicensePlate = dto.licensePlate().trim().toUpperCase();

        log.info("Creating vehicle with license plate: {}", toUpperCaseLicensePlate);

        if (vehicleRepository.existsVehicleByLicensePlate(toUpperCaseLicensePlate)) {
            throw new LicensePlateDuplicated("Vehicle with license plate: " + toUpperCaseLicensePlate + " is already registered");
        }
        BigDecimal dollarRate = currencyService.getUSDDollarRate();
        BigDecimal priceUSD = dto.price().divide(dollarRate, 4, RoundingMode.HALF_UP);

        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate(toUpperCaseLicensePlate);
        vehicle.setBrand(dto.brand());
        vehicle.setYear(dto.year());
        vehicle.setColor(dto.color());
        vehicle.setPrice(priceUSD);
        vehicle.setActive(true);

        return mapToResponse(vehicleRepository.save(vehicle), dollarRate);
    }

    //Implementation of filtering the vehicles. In this is possible to get a full list of registers or filtered by vehicles attributes
    @Override
    @Transactional(readOnly = true)
    public Page<VehicleResponseDTO> listWithFilter(String brand, Integer year, String color, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        //Using Specification to filter the vehicles by attributes
        Specification<Vehicle> spec = VehicleSpecifications.byFilters(brand, year, color, minPrice, maxPrice)
                .and((root, query, cb) -> cb.equal(root.get("active"), true));

        BigDecimal dollarRate = currencyService.getUSDDollarRate();
        log.info("Listing vehicles with filters");

        return vehicleRepository.findAll(spec, pageable).map(v -> mapToResponse(v, dollarRate));
    }

    //Implementation of getting a specific vehicle by it id
    @Override
    @Transactional(readOnly = true)
    public VehicleResponseDTO findById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with Id: " + id));

        if (!vehicle.isActive()) {
            throw new ResourceNotFoundException("Vehicle not found with Id: " + id);
        }

        log.info("Finding vehicle with Id: {}", id);
        return mapToResponse(vehicle, currencyService.getUSDDollarRate());
    }

    //Implementation of updating all attributes of the vehicle
    @Override
    @Transactional
    public VehicleResponseDTO updateTotal(Long id, VehicleUpdateDTO dto) {
        String toUpperCaseLicensePlate = dto.licensePlate().trim().toUpperCase();

        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with Id: " + id));

        if (!vehicle.isActive()) {
            throw new ResourceNotFoundException("Vehicle not found with Id: " + id);
        }

        log.info("Updating vehicle with Id: {}", id);

        validateNewLicensePlate(id, toUpperCaseLicensePlate);

        vehicle.setLicensePlate(toUpperCaseLicensePlate);
        vehicle.setBrand(dto.brand());
        vehicle.setYear(dto.year());
        vehicle.setColor(dto.color());

        BigDecimal dollarRate = currencyService.getUSDDollarRate();
        if (dto.priceBRL() != null) {
            vehicle.setPrice(dto.priceBRL().divide(dollarRate, 4, RoundingMode.HALF_UP));
        }

        return mapToResponse(vehicleRepository.save(vehicle), dollarRate);
    }

    //Implementation of updating partially attributes of the vehicle
    @Override
    @Transactional
    public VehicleResponseDTO updateParcial(Long id, VehicleUpdateDTO dto) {

        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with Id: " + id));

        if (!vehicle.isActive()) {
            throw new ResourceNotFoundException("Vehicle not found with Id: " + id);
        }

        log.info("Updating partially vehicle with Id: {}", id);

        if (dto.licensePlate() != null) {
            String toUpperCaseLicensePlate = dto.licensePlate().trim().toUpperCase();
            validateNewLicensePlate(id, toUpperCaseLicensePlate);
            vehicle.setLicensePlate(toUpperCaseLicensePlate);
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

    //Implementation of delete a vehicle. It performs a soft-delete, setting active field to false and keeping the register on database
    @Override
    @Transactional
    public void delete(Long id) {
        Optional<Vehicle> vehicle = vehicleRepository.findById(id);
        if (vehicle.isEmpty()) {
            throw new ResourceNotFoundException("Vehicle not found with Id: " + id);
        }

        log.info("Soft-delete of vehicle id: {}", vehicle.get().getIdVehicle());
        
        vehicleRepository.deleteById(id);
    }

    //Implementation of generating a report grouped by vehicle brand
    @Override
    @Transactional(readOnly = true)
    public List<ReportBrandDTO> getReportByBrand() {
        log.info("Getting report by brand");
        return vehicleRepository.getReportByBrand();
    }


    //Method that helps to validate if a license place is duplicated in another vehicle
    private void validateNewLicensePlate(Long currentId, String newLicensePlate) {
        if (vehicleRepository.existsByLicensePlateAndIdVehicleNot(newLicensePlate, currentId)) {
            throw new LicensePlateDuplicated("Vehicle with license plate: " + newLicensePlate + " is already registered by another vehicle");
        }
    }

    //Method that helps to convert the Vehicle object into a DTO response
    private VehicleResponseDTO mapToResponse(Vehicle vehicle, BigDecimal dollarRate) {
        BigDecimal priceBRL = vehicle.getPrice().multiply(dollarRate).setScale(2, RoundingMode.HALF_UP);
        return new VehicleResponseDTO(
                vehicle.getIdVehicle(), vehicle.getLicensePlate(), vehicle.getBrand(), vehicle.getYear(), vehicle.getColor(),
                vehicle.getPrice().setScale(2, RoundingMode.HALF_UP), priceBRL, dollarRate, vehicle.isActive()
        );
    }
}
