package org.example.desafio.desafiotinnova.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.desafio.desafiotinnova.exception.CurrencyServiceUnavailableException;
import org.example.desafio.desafiotinnova.service.contract.CurrencyService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {

    private final RestClient restClient = RestClient.create();

    @Override
    @Cacheable(value = "dollarRate", key = "'current'")
    public BigDecimal getUSDDollarRate() {
        log.info("Getting USD Dollar rate from Awesome API...");
        try {
            Map<String, Object> response = restClient.get()
                    .uri("https://economia.awesomeapi.com.br/json/last/USD-BRL")
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});

            if (response != null && response.containsKey("USDBRL")) {
                @SuppressWarnings("unchecked")
                Map<String, String> data = (Map<String, String>) response.get("USDBRL");
                return new BigDecimal(data.get("bid"));
            }
        } catch (Exception e) {
            log.warn("Failed to get USD Dollar rate from Awesome API. Trying fallback API...", e);
            return getFromFallBackAPI();
        }
        throw new CurrencyServiceUnavailableException("Failed to get USD Dollar rate from main API.");
    }

    private BigDecimal getFromFallBackAPI() {
        try {
            Map<String, Object> response = restClient.get()
                    .uri("https://api.frankfurter.app/latest?from=USD&to=BRL")
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});

            if (response != null && response.containsKey("rates")) {
                @SuppressWarnings("unchecked")
                Map<String, Double> rates = (Map<String, Double>) response.get("rates");
                return BigDecimal.valueOf(rates.get("BRL"));
            }
        } catch (Exception e) {
            log.error("Critical failure: both API's are unavailable.", e);
        }
        throw new CurrencyServiceUnavailableException("Currency rates services are unavailable at the moment. Please try again later");
    }
}
