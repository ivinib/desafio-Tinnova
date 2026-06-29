package org.example.desafio.desafiotinnova.dto.response;


import java.math.BigDecimal;

public record ReportBrandDTO(
        String brand,
        Long quantityVehicles,
        Double avgPrice
) {
    public BigDecimal convertToBigDecimal(){
        return avgPrice != null ? BigDecimal.valueOf(avgPrice) :BigDecimal.ZERO;
    }
}
