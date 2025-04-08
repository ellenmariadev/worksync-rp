package com.example.worksync.exceptions.core;

import com.example.worksync.exceptions.ConflictException;
import com.example.worksync.exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @SuppressWarnings({ "null", "deprecation" })
    @Test
    void testHandleConflictException() {
        ConflictException ex = new ConflictException("Recurso já existe");
        ResponseEntity<ErrorResponse> response = handler.handleConflictException(ex);

        assertEquals(409, response.getStatusCodeValue());
        assertEquals("Conflict", response.getBody().getError());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody());
        assertEquals("Recurso já existe", response.getBody().getMessage());
    }

    @SuppressWarnings({ "null", "deprecation" })
    @Test
    void testHandleAuthenticationException() {
        BadCredentialsException ex = new BadCredentialsException("Bad credentials");
        ResponseEntity<ErrorResponse> response = handler.handleAuthenticationException(ex);

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Authentication Error", response.getBody().getError());
        assertEquals("Invalid username or password", response.getBody().getMessage());
    }

    @SuppressWarnings("null")
    @Test
    void testHandleNotFoundException() {
        NotFoundException ex = new NotFoundException("Item não encontrado");
        ResponseEntity<ErrorResponse> response = handler.handleNotFoundException(ex);

        assertEquals(404, response.getStatusCode().value());
        assertEquals("Not found", response.getBody().getError());
        assertEquals("Item não encontrado", response.getBody().getMessage());
    }

    @SuppressWarnings({ "null", "deprecation" })
    @Test
    void testHandleAllExceptions() {
        Exception ex = new Exception("Erro genérico");
        ResponseEntity<ErrorResponse> response = handler.handleAllExceptions(ex);

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Internal Server Error", response.getBody().getError());
        assertEquals("Erro genérico", response.getBody().getMessage());
    }
}
