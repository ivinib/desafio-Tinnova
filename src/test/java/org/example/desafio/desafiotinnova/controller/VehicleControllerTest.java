package org.example.desafio.desafiotinnova.controller;

import org.example.desafio.desafiotinnova.exception.LicencePlateDuplicated;
import org.example.desafio.desafiotinnova.service.contract.VehicleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class VehicleControllerTest {
    @Autowired
    private MockMvc mockMvc;

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
}
