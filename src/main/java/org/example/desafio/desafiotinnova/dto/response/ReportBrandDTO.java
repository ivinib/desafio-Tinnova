package org.example.desafio.desafiotinnova.dto.response;


import java.math.BigDecimal;

public record ReportBrandDTO(
        String brand,
        Long quantityVehicles,
        BigDecimal avgPrice
) {}
