package org.example.desafio.desafiotinnova.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record VehicleUpdateDTO(

        @Pattern(regexp = "^[A-Z]{3}[0-9][A-Z0-9][0-9]{2}$", message = "Licence place format must be AAAA-1111 or AAA-1A11")
        String licencePlate,

        String brand,

        @Min(value = 1900, message = "Year must be greater than 1900")
        Integer year,

        String color,

        @Positive(message = "Price in Brazilian Real currency must be greater than zero")
        BigDecimal priceBRL
) {}
