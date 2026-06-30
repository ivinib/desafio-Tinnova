package org.example.desafio.desafiotinnova.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.desafio.desafiotinnova.dto.request.VehicleCreateDTO;
import org.example.desafio.desafiotinnova.dto.request.VehicleUpdateDTO;
import org.example.desafio.desafiotinnova.dto.response.ReportBrandDTO;
import org.example.desafio.desafiotinnova.dto.response.VehicleResponseDTO;
import org.example.desafio.desafiotinnova.exception.LicencePlateDuplicated;
import org.example.desafio.desafiotinnova.exception.ResourceNotFoundException;
import org.example.desafio.desafiotinnova.service.contract.VehicleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class VehicleControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VehicleService vehicleService;

    @Test
    @DisplayName("Should return error 401 when not authorized")
    void testShouldReturn401WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/veiculos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status", is(401)))
                .andExpect(jsonPath("$.error", is("Unauthorized")))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return error 403 when user has no permission")
    void testShouldReturn403WhenUserHasNoPermission() throws Exception {
        String jsonBody = "{\"licencePlate\":\"ABC1D23\",\"brand\":\"Ford\",\"year\":2023,\"color\":\"Preto\",\"price\":50000}";

        mockMvc.perform(post("/veiculos")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status", is(403)))
                .andExpect(jsonPath("$.error", is("Forbidden")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return error 409 when trying to register a duplicated licence plate")
    void testShouldReturn409WhenTryingToRegisterDuplicatedLicencePlate() throws Exception {
        String jsonBody = "{\"licencePlate\":\"ABC1D23\",\"brand\":\"Ford\",\"year\":2023,\"color\":\"Preto\",\"price\":50000}";

        doThrow(new LicencePlateDuplicated("Licence plate is already registered"))
                .when(vehicleService).create(any());

        mockMvc.perform(post("/veiculos")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 201 Created and response payload when creating vehicle as ADMIN")
    void testCreateVehicleWithSuccess() throws Exception {
        VehicleCreateDTO dto = new VehicleCreateDTO("ABC1D23", "Ford", 2023, "Preto", BigDecimal.valueOf(50000.00));
        VehicleResponseDTO responseMock = new VehicleResponseDTO(1L, "ABC1D23", "Ford", 2023, "Preto", BigDecimal.ONE, BigDecimal.TEN, BigDecimal.ONE, true);

        when(vehicleService.create(any(VehicleCreateDTO.class))).thenReturn(responseMock);

        mockMvc.perform(post("/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idVehicle", is(1)))
                .andExpect(jsonPath("$.licencePlate", is("ABC1D23")));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return 200 OK and filtered vehicles on paginated list")
    void testListFilteredWithSuccess() throws Exception {
        VehicleResponseDTO responseMock = new VehicleResponseDTO(1L, "ABC1D23", "Ford", 2023, "Preto", BigDecimal.ONE, BigDecimal.TEN, BigDecimal.ONE, true);
        when(vehicleService.listWithFilter(any(), any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(responseMock)));

        mockMvc.perform(get("/veiculos")
                        .param("brand", "Ford")
                        .param("color", "Preto")
                        .param("minPrice", "20000")
                        .param("maxPrice", "60000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].brand", is("Ford")));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return 200 OK and detail a specific vehicle when ID is valid")
    void testFindByIdWithSuccess() throws Exception {
        VehicleResponseDTO responseMock = new VehicleResponseDTO(1L, "ABC1D23", "Ford", 2023, "Preto", BigDecimal.ONE, BigDecimal.TEN, BigDecimal.ONE, true);
        when(vehicleService.findById(1L)).thenReturn(responseMock);

        mockMvc.perform(get("/veiculos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idVehicle", is(1)))
                .andExpect(jsonPath("$.brand", is("Ford")));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return 404 Not Found when ID is invalid or inactive")
    void testFindByIdShouldReturn404WhenNotFound() throws Exception {
        when(vehicleService.findById(99L)).thenThrow(new ResourceNotFoundException("Vehicle not found"));

        mockMvc.perform(get("/veiculos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 200 OK when updating vehicle totally (PUT) as ADMIN")
    void testUpdateTotalWithSuccess() throws Exception {
        VehicleUpdateDTO updateDto = new VehicleUpdateDTO("XYZ9E87", "Chevrolet", 2024, "Branco", BigDecimal.valueOf(60000.00));
        VehicleResponseDTO responseMock = new VehicleResponseDTO(1L, "XYZ9E87", "Chevrolet", 2024, "Branco", BigDecimal.ONE, BigDecimal.TEN, BigDecimal.ONE, true);

        when(vehicleService.updateTotal(eq(1L), any(VehicleUpdateDTO.class))).thenReturn(responseMock);

        mockMvc.perform(put("/veiculos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brand", is("Chevrolet")))
                .andExpect(jsonPath("$.licencePlate", is("XYZ9E87")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 200 OK when updating vehicle partially (PATCH) as ADMIN")
    void testUpdateParcialWithSuccess() throws Exception {
        VehicleUpdateDTO updateDto = new VehicleUpdateDTO(null, null, null, "Vermelho", null);
        VehicleResponseDTO responseMock = new VehicleResponseDTO(1L, "ABC1D23", "Ford", 2023, "Vermelho", BigDecimal.ONE, BigDecimal.TEN, BigDecimal.ONE, true);

        when(vehicleService.updateParcial(eq(1L), any(VehicleUpdateDTO.class))).thenReturn(responseMock);

        mockMvc.perform(patch("/veiculos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.color", is("Vermelho")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 204 No Content when deleting a vehicle as ADMIN")
    void testDeleteWithSuccess() throws Exception {
        doNothing().when(vehicleService).delete(1L);

        mockMvc.perform(delete("/veiculos/1"))
                .andExpect(status().isNoContent());

        verify(vehicleService, times(1)).delete(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return 200 OK and brand report list when accessing as USER or ADMIN")
    void testGetReportByBrandWithSuccess() throws Exception {
        List<ReportBrandDTO> mockReport = List.of(new ReportBrandDTO("Ford", 1L, 50000.00));
        when(vehicleService.getReportByBrand()).thenReturn(mockReport);

        mockMvc.perform(get("/veiculos/relatorios/por-marca"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].brand", is("Ford")))
                .andExpect(jsonPath("$[0].quantityVehicles", is(1)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 400 Bad Request with structured field errors when payload validation fails")
    void testShouldReturn400WhenPayloadFieldsAreInvalid() throws Exception {
        String invalidJson = "{\"licencePlate\":\"ERRADA\",\"brand\":\"\",\"year\":1850,\"color\":\"\",\"price\":-100}";
        mockMvc.perform(post("/veiculos")
                .content(invalidJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.errors").exists());
    }
}
