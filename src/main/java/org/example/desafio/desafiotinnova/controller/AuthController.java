package org.example.desafio.desafiotinnova.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.desafio.desafiotinnova.dto.request.LoginRequestDTO;
import org.example.desafio.desafiotinnova.dto.response.TokenResponseDTO;
import org.example.desafio.desafiotinnova.security.JwtTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody @Valid LoginRequestDTO loginRequestDto) {

        var authenticationToken = new UsernamePasswordAuthenticationToken(
                loginRequestDto.username(),
                loginRequestDto.password()
        );

        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER")
                .replace("ROLE_", "");

        // 3. Gera o Token JWT contendo as permissões adequadas
        String token = tokenService.generateToken(authentication.getName(), role);

        return ResponseEntity.ok(new TokenResponseDTO(token, "Bearer"));
    }
}
