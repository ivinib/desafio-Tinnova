package org.example.desafio.desafiotinnova.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.desafio.desafiotinnova.dto.request.LoginRequestDTO;
import org.example.desafio.desafiotinnova.dto.response.TokenResponseDTO;
import org.example.desafio.desafiotinnova.security.JwtTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenService tokenService;

    @Operation(summary = "Autenticar usuário", description = "Recebe as credenciais e devolve um token JWT válido com a role do usuário (ADMIN ou USER).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticação efetuada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas ou usuário não cadastrado")
    })
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody @Valid LoginRequestDTO loginRequestDto) {

        var authenticationToken = new UsernamePasswordAuthenticationToken(
                loginRequestDto.username(),
                loginRequestDto.password()
        );

        Authentication authentication;

        try {
            authentication = authenticationManager.authenticate(authenticationToken);
        } catch (BadCredentialsException ex) {
            log.warn("Authentication failed for user: {}", loginRequestDto.username());

            throw new BadCredentialsException("Username or password incorrect.");
        }

        log.info("User {} authenticated with role {}", authentication.getName(), authentication.getAuthorities());

        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER")
                .replace("ROLE_", "");

        String token = tokenService.generateToken(authentication.getName(), role);

        return ResponseEntity.ok(new TokenResponseDTO(token, "Bearer"));
    }
}
