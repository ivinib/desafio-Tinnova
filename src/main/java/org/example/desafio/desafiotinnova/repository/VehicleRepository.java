package org.example.desafio.desafiotinnova.repository;

import org.example.desafio.desafiotinnova.dto.response.ReportBrandDTO;
import org.example.desafio.desafiotinnova.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long>, JpaSpecificationExecutor<Vehicle> {
    boolean existsVehicleByLicensePlate(String licensePlate);
    boolean existsByLicensePlateAndIdVehicleNot(String licensePlate, Long idVehicle);

    //Custom query to generate the report grouping by vehicle brand, counting how many vehicles there are for each brand and calculating the average price
    @Query("SELECT new org.example.desafio.desafiotinnova.dto.response.ReportBrandDTO(v.brand, COUNT(v), AVG(v.price)) " +
           "FROM Vehicle v GROUP BY v.brand")
    List<ReportBrandDTO> getReportByBrand();

}
