package org.example.desafio.desafiotinnova.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import lombok.*;

import java.math.BigDecimal;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "tb_vehicle", uniqueConstraints = {@UniqueConstraint(columnNames = "licence_plate")})
@SQLDelete(sql = "UPDATE tb_vehicle SET ativo = false WHERE id_vehicle = ?")
@Data
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

    @Column(name = "vehicle_year", nullable = false)
    private Integer year;

    @Column(name = "color", nullable = false)
    private String color;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "active", nullable = false)
    private boolean active = true;
}
