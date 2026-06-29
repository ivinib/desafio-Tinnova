package org.example.desafio.desafiotinnova.service.impl;

import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final CurrencyService currencyService;


    @Override
    @Transactional
    public VehicleResponseDTO create(VehicleCreateDTO dto) {
        if (vehicleRepository.existsVehicleByLicencePlate(dto.licencePlate())) {
            throw new LicencePlateDuplicated("Vehicle with licence plate: " + dto.licencePlate() + " it's already registered");
        }
        BigDecimal dollarRate = currencyService.getUSDDollarRate();
        BigDecimal priceBRL = dto.price().divide(dollarRate, 4, RoundingMode.HALF_UP);

        Vehicle veiculo = new Vehicle();
        veiculo.setLicencePlate(dto.licencePlate());
        veiculo.setBrand(dto.brand());
        veiculo.setYear(dto.year());
        veiculo.setColor(dto.color());
        veiculo.setPrice(priceBRL);

        return mapToResponse(vehicleRepository.save(veiculo), dollarRate);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VehicleResponseDTO> listWithFilter(String marca, Integer ano, String cor, BigDecimal minPreco, BigDecimal maxPreco, Pageable pageable) {
        Specification<Vehicle> spec = VehicleSpecifications.byFilters(marca, ano, cor, minPreco, maxPreco);
        BigDecimal dollarRate = currencyService.getUSDDollarRate();
        return vehicleRepository.findAll(spec, pageable).map(v -> mapToResponse(v, dollarRate));
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleResponseDTO findById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with Id: " + id));
        return mapToResponse(vehicle, currencyService.getUSDDollarRate());
    }


    @Override
    @Transactional
    public VehicleResponseDTO updateTotal(Long id, VehicleUpdateDTO dto) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with Id: " + id));

        vehicle.setLicencePlate(dto.licencePlate());
        vehicle.setBrand(dto.brand());
        vehicle.setYear(dto.year());
        vehicle.setColor(dto.color());

        BigDecimal dollarRate = currencyService.getUSDDollarRate();
        vehicle.setPrice(dto.priceBRL().divide(dollarRate, 4, RoundingMode.HALF_UP));

        return mapToResponse(vehicleRepository.save(vehicle), dollarRate);
    }

    @Override
    @Transactional
    public VehicleResponseDTO updateParcial(Long id, VehicleUpdateDTO dto) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with Id: " + id));

        if (dto.licencePlate() != null) vehicle.setLicencePlate(dto.licencePlate());
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
    public void delete(Long id) {
        if (!vehicleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Vehicle not found.");
        }
        vehicleRepository.deleteById(id);
    }

    @Override
    public List<ReportBrandDTO> getReportByBrand() {
        return vehicleRepository.getReportByBrand();
    }

    private VehicleResponseDTO mapToResponse(Vehicle vehicle, BigDecimal dollarRate) {
        BigDecimal priceBRL = vehicle.getPrice().multiply(dollarRate).setScale(2, RoundingMode.HALF_UP);
        return new VehicleResponseDTO(
                vehicle.getIdVehicle(), vehicle.getLicencePlate(), vehicle.getBrand(), vehicle.getYear(), vehicle.getColor(),
                vehicle.getPrice().setScale(2, RoundingMode.HALF_UP), priceBRL, dollarRate
        );
    }
}
