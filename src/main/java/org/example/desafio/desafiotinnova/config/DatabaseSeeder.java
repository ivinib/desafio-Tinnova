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

    @EventListener(ContextRefreshedEvent.class)
    public void seed() {
        if (vehicleRepository.count() == 0) {
            Vehicle v1 = new Vehicle(null, "ABC1D23", "Ford", 2023, "Preto", BigDecimal.valueOf(10000.00), true);
            Vehicle v2 = new Vehicle(null, "XYZ9E87", "Chevrolet", 2024, "Branco", BigDecimal.valueOf(15000.00), true);
            Vehicle v3 = new Vehicle(null, "MNO4V56", "Fiat", 2022, "Vermelho", BigDecimal.valueOf(8500.00), true);
            Vehicle v4 = new Vehicle(null, "KJG9A88", "Volkswagen", 2024, "Azul", BigDecimal.valueOf(19000.00), true);

            vehicleRepository.saveAll(List.of(v1, v2, v3, v4));
        }
    }
}
