package org.example.desafio.desafiotinnova.model;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse (
        LocalDateTime timestamp,
        int status,
        String error,
        String code,
        String message,
        String path,
        String traceId,
        Map<String, String> validationErrors
){}
