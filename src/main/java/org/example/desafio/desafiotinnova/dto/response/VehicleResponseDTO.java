package org.example.desafio.desafiotinnova.dto.response;

import java.math.BigDecimal;

public record VehicleResponseDTO(
        Long idVehicle,
        String licencePlate,
        String brand,
        Integer year,
        String color,
        BigDecimal priceInUSD,
        BigDecimal priceInBRL,
        BigDecimal currency,
        Boolean active
) {}
