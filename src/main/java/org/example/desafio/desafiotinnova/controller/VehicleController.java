package org.example.desafio.desafiotinnova.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.desafio.desafiotinnova.dto.request.VehicleCreateDTO;
import org.example.desafio.desafiotinnova.dto.request.VehicleUpdateDTO;
import org.example.desafio.desafiotinnova.dto.response.ReportBrandDTO;
import org.example.desafio.desafiotinnova.dto.response.VehicleResponseDTO;
import org.example.desafio.desafiotinnova.service.contract.VehicleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/veiculos")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @Operation(summary = "Cadastrar um novo veículo", description = "Operação restrita para administradores (ADMIN). Salva o preço convertido para USD.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Veículo cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos (Erros de validação)"),
            @ApiResponse(responseCode = "401", description = "Não autenticado (Token ausente ou inválido)"),
            @ApiResponse(responseCode = "403", description = "Não autorizado (Usuário sem papel de ADMIN)"),
            @ApiResponse(responseCode = "409", description = "Conflito: Placa já cadastrada no sistema")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VehicleResponseDTO> create(@RequestBody @Valid VehicleCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vehicleService.create(dto));
    }


    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Page<VehicleResponseDTO>> listFiltered(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @PageableDefault(size = 10, sort = "brand") Pageable pageable) {

        Page<VehicleResponseDTO> response = vehicleService.listWithFilter(brand, year, color, minPrice, maxPrice, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<VehicleResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.findById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VehicleResponseDTO> updateTotal(@PathVariable Long id, @RequestBody @Valid VehicleUpdateDTO dto) {
        return ResponseEntity.ok(vehicleService.updateTotal(id, dto));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VehicleResponseDTO> atualizarParcial(@PathVariable Long id, @RequestBody VehicleUpdateDTO dto) {
        return ResponseEntity.ok(vehicleService.updateParcial(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        vehicleService.delete(id);
    }

    @GetMapping("/relatorios/por-marca")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<ReportBrandDTO>> getReportByBrand() {
        return ResponseEntity.ok(vehicleService.getReportByBrand());
    }
}
