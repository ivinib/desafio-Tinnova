package org.example.desafio.desafiotinnova.config;

import lombok.RequiredArgsConstructor;

import org.example.desafio.desafiotinnova.model.Vehicle;
import org.example.desafio.desafiotinnova.repository.VehicleRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import java.math.BigDecimal;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Profile("!test")
public class DatabaseSeeder {
    private final VehicleRepository vehicleRepository;

    //Helper created to populate database with few registers to have initial data on database
    @EventListener(ContextRefreshedEvent.class)
    public void seed() {
        if (vehicleRepository.count() == 0) {
            Vehicle v1 = new Vehicle(null, "ABC1D23", "Ford", 2023, "Preto", BigDecimal.valueOf(10000.00), true);
            Vehicle v2 = new Vehicle(null, "XYZ9E87", "Chevrolet", 2024, "Branco", BigDecimal.valueOf(15000.00), true);
            Vehicle v3 = new Vehicle(null, "MNO4V56", "Fiat", 2022, "Vermelho", BigDecimal.valueOf(8500.00), true);
            Vehicle v4 = new Vehicle(null, "KJG9A88", "Volkswagen", 2020, "Azul", BigDecimal.valueOf(19000.00), true);
            Vehicle v5 = new Vehicle(null, "DRC4253", "Volkswagen", 2012, "Branco", BigDecimal.valueOf(11000.00), false);
            Vehicle v6 = new Vehicle(null, "AXD3321", "Volkswagen", 2026, "Vermelho", BigDecimal.valueOf(34000.00), true);
            Vehicle v7 = new Vehicle(null, "QQA3241", "Fiat", 2023, "Prata", BigDecimal.valueOf(22000.00), true);
            Vehicle v8 = new Vehicle(null, "GBV3268", "Chevrolet", 2022, "Branco", BigDecimal.valueOf(18000.00), true);
            Vehicle v9 = new Vehicle(null, "TRE6789", "Honda", 2025, "Cinza", BigDecimal.valueOf(43000.00), true);
            Vehicle v10 = new Vehicle(null, "KJH5432", "Honda", 2024, "Vermelho", BigDecimal.valueOf(37000.00), true);

            vehicleRepository.saveAll(List.of(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10));
        }
    }
}
