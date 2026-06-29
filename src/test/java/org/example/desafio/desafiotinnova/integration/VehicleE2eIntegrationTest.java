package org.example.desafio.desafiotinnova.integration;

import com.jayway.jsonpath.JsonPath;
import org.example.desafio.desafiotinnova.repository.VehicleRepository;
import org.example.desafio.desafiotinnova.service.contract.CurrencyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class VehicleE2eIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VehicleRepository vehicleRepository;

    @MockBean
    private CurrencyService currencyService;

    @BeforeEach
    void setUp() {
        // Limpa a tabela antes de rodar o fluxo integrado para evitar colisões de ID ou placas
        vehicleRepository.deleteAll();

        Mockito.when(currencyService.getUSDDollarRate()).thenReturn(BigDecimal.valueOf(5.00));
    }

    @Test
    @DisplayName("Validate flow end-to-end")
    void testValidateCompleteFlowEndToEnd() throws Exception {

        // Get token as ADMIN
        String loginPayload = "{\"username\":\"admin\",\"password\":\"admin123\"}";

        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .content(loginPayload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String token = JsonPath.read(loginResult.getResponse().getContentAsString(), "$.token");
        String authorizationHeader = "Bearer " + token;

        // Register a new vehicle in ADMIN role
        String vehiclePayload = "{\"licencePlate\":\"EXS4A56\",\"brand\":\"Volkswagen\",\"year\":2024,\"color\":\"Azul\",\"price\":95000}";

        MvcResult createResult = mockMvc.perform(post("/veiculos")
                        .header("Authorization", authorizationHeader)
                        .content(vehiclePayload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        // Get the id of registered vehicle
        Integer idGerado = JsonPath.read(createResult.getResponse().getContentAsString(), "$.idVehicle");

        // Filtering with brand and color
        mockMvc.perform(get("/veiculos")
                        .header("Authorization", authorizationHeader)
                        .param("brand", "Volkswagen")
                        .param("color", "Azul")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].licencePlate", is("EXS4A56")));

        // Get all details of one vehicle by it id
        mockMvc.perform(get("/veiculos/" + idGerado)
                        .header("Authorization", authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idVehicle", is(idGerado)))
                .andExpect(jsonPath("$.brand", is("Volkswagen")));
    }
}
