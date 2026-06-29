package org.example.desafio.desafiotinnova.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "tb_vehicle", uniqueConstraints = {@UniqueConstraint(columnNames = "licence_plate")})

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vehicle")
    private Long idVehicle;

    @Column(name = "licence_plate", nullable = false, unique = true)
    private String licencePlate;

    @Column(name = "brand", nullable = false)
    private String brand;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "color", nullable = false)
    private String cor;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private boolean ativo = true;
}
