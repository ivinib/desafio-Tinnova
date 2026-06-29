package org.example.desafio.desafiotinnova;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class DesafioTinnovaApplication {

    public static void main(String[] args) {
        SpringApplication.run(DesafioTinnovaApplication.class, args);
    }

}
