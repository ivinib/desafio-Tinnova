package org.example.desafio.desafiotinnova.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.Map;
//Standardized error message
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorPayload(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> errors
) {
    public ErrorPayload(int status, String error, String message, String path) {
        this(LocalDateTime.now(), status, error, message, path, null);
    }
}
