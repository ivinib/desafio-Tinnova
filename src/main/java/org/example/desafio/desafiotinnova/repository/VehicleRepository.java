package org.example.desafio.desafiotinnova.repository;

import org.example.desafio.desafiotinnova.dto.response.ReportBrandDTO;
import org.example.desafio.desafiotinnova.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    boolean existsVehicleByLicencePlate(String licencePlate);

    @Query("SELECT new org.example.desafio.desafiotinnova.dto.response.ReportBrandDTO(v.brand, COUNT(v), AVG(v.price)) " +
           "FROM Vehicle v GROUP BY v.brand")
    List<ReportBrandDTO> getReportByBrand();
}
