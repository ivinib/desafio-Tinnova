package org.example.desafio.desafiotinnova.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Libera todos os endpoints da API
                        .allowedOrigins("http://localhost:4200") // Permite apenas o seu front-end Angular
                        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS") // Libera os verbos HTTP utilizados
                        .allowedHeaders("*") // Libera todos os cabeçalhos (incluindo o Authorization)
                        .allowCredentials(true); // Permite o tráfego de cookies/sessões se necessário
            }
        };
    }
}
