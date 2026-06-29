package org.example.desafio.desafiotinnova.service.contract;

import org.example.desafio.desafiotinnova.dto.request.VehicleCreateDTO;
import org.example.desafio.desafiotinnova.dto.request.VehicleUpdateDTO;
import org.example.desafio.desafiotinnova.dto.response.ReportBrandDTO;
import org.example.desafio.desafiotinnova.dto.response.VehicleResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface VehicleService {
    VehicleResponseDTO create(VehicleCreateDTO dto);
    Page<VehicleResponseDTO> listWithFilter(String marca, Integer ano, String cor, BigDecimal minPreco, BigDecimal maxPreco, Pageable pageable);
    VehicleResponseDTO findById(Long id);
    VehicleResponseDTO updateTotal(Long id, VehicleUpdateDTO dto);
    VehicleResponseDTO updateParcial(Long id, VehicleUpdateDTO dto);
    void delete(Long id);
    List<ReportBrandDTO> getReportByBrand();
}
