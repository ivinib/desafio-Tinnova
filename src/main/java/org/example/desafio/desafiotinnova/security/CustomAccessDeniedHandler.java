package org.example.desafio.desafiotinnova.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.desafio.desafiotinnova.exception.ErrorPayload;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        ErrorPayload error = new ErrorPayload(
                LocalDateTime.now(),
                HttpServletResponse.SC_FORBIDDEN,
                "Forbidden",
                "You do not have permission to access this resource.",
                request.getRequestURI(),
                null
        );

        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        response.getWriter().write(mapper.writeValueAsString(error));
    }
}
