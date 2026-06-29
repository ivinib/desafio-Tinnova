package org.example.desafio.desafiotinnova.service;

import org.example.desafio.desafiotinnova.exception.CurrencyServiceUnavailableException;
import org.example.desafio.desafiotinnova.service.impl.CurrencyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CurrencyServiceTest {

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @InjectMocks
    private CurrencyServiceImpl currencyService;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(currencyService, "restClient", restClient);
    }

    @Test
    @DisplayName("Should return the currency rate when main API response is valid")
    void testCurrencyRate() {
        Map<String, Object> currencyDetail = Map.of("bid", "5.2534");
        Map<String, Object> mockAnswer = Map.of("USDBRL", currencyDetail);

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(contains("awesomeapi"))).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(mockAnswer);

        BigDecimal dollarRate = currencyService.getUSDDollarRate();

        assertThat(dollarRate).isNotNull();
        assertThat(dollarRate).isEqualByComparingTo("5.2534");
    }

    @Test
    @DisplayName("Should use fallback API when the main API call fails")
    void testCurrencyRateWithFallback() {
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(contains("awesomeapi"))).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenThrow(new RuntimeException("Main API Down"));

        Map<String, Object> rates = Map.of("BRL", 5.10);
        Map<String, Object> fallbackAnswer = Map.of("rates", rates);

        RestClient.RequestHeadersUriSpec fallbackRequestHeadersUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        when(requestHeadersUriSpec.uri(contains("frankfurter"))).thenReturn(fallbackRequestHeadersUriSpec);

        lenient().when(fallbackRequestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(fallbackAnswer);

        BigDecimal dollarRate = currencyService.getUSDDollarRate();

        assertThat(dollarRate).isNotNull();
        assertThat(dollarRate).isEqualByComparingTo("5.10");
    }

    @Test
    @DisplayName("Should throw CurrencyServiceUnavailableException when both API's fail")
    void testShouldThrowExceptionWhenBothApisFail() {
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenThrow(new RuntimeException("Critical failure"));

        assertThrows(CurrencyServiceUnavailableException.class, () -> {
            currencyService.getUSDDollarRate();
        });
    }
}