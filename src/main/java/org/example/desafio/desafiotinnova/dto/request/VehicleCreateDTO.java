package org.example.desafio.desafiotinnova.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;

public record VehicleCreateDTO (
    @NotBlank(message = "Licende plate is required")
    @Pattern(regexp = "^[A-Z]{3}[0-9][A-Z0-9][0-9]{2}$", message = "Licence place format must be AAAA1111 or AAA1A11")
    String licencePlate,

    @NotBlank(message = "Vehicle brand is required")
    String brand,

    @NotNull(message = "Vehicle year is required")
    @Min(value = 1900, message = "Year must be greater than 1900")
    Integer year,

    @NotBlank(message = "Vehicle color is required")
    String color,

    @NotNull(message = "Vehicle price in Brazilian Real currency is required")
    @Positive(message = "Price must be greater than zero")
    BigDecimal price
){}