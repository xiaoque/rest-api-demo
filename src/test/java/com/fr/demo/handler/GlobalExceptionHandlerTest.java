package com.fr.demo.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fr.demo.exception.ProjectNotFoundException;
import com.fr.demo.model.dto.ErrorResponse;

public class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    @Test
    void handleProjectNotFoundException_ShouldReturnNotFoundResponse() {
        ProjectNotFoundException ex = new ProjectNotFoundException(1);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleProjectNotFoundException(ex);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().getStatus());
    }

    @Test
    void handleGeneralException_ShouldReturnInternalServerErrorResponse() {
        Exception ex = new Exception("Test exception");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGenericException(ex);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(500, response.getBody().getStatus());
    }
}
