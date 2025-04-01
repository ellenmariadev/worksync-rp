package com.example.worksync.exceptions.core;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

class ErrorResponseTest {

    @Test
    void constructor_shouldSetAllFields() {
        LocalDateTime timestamp = LocalDateTime.now();
        int status = 400;
        String error = "Bad Request";
        String message = "Invalid input.";

        ErrorResponse errorResponse = new ErrorResponse(timestamp, status, error, message);

        assertNotNull(errorResponse.getTimestamp());
        assertEquals(timestamp, errorResponse.getTimestamp());
        assertEquals(status, errorResponse.getStatus());
        assertEquals(error, errorResponse.getError());
        assertEquals(message, errorResponse.getMessage());
    }

    @Test
    void setters_shouldUpdateFields() {
        LocalDateTime initialTimestamp = LocalDateTime.now();
        int initialStatus = 400;
        String initialError = "Bad Request";
        String initialMessage = "Invalid input.";

        ErrorResponse errorResponse = new ErrorResponse(initialTimestamp, initialStatus, initialError, initialMessage);

        LocalDateTime newTimestamp = LocalDateTime.now().plusDays(1);
        int newStatus = 500;
        String newError = "Internal Server Error";
        String newMessage = "An unexpected error occurred.";

        errorResponse.setTimestamp(newTimestamp);
        errorResponse.setStatus(newStatus);
        errorResponse.setError(newError);
        errorResponse.setMessage(newMessage);

        assertEquals(newTimestamp, errorResponse.getTimestamp());
        assertEquals(newStatus, errorResponse.getStatus());
        assertEquals(newError, errorResponse.getError());
        assertEquals(newMessage, errorResponse.getMessage());
    }

    @Test
    void getters_shouldReturnCorrectValues(){
        LocalDateTime timestamp = LocalDateTime.now();
        int status = 404;
        String error = "Not Found";
        String message = "Resource not found";

        ErrorResponse errorResponse = new ErrorResponse(timestamp, status, error, message);

        assertEquals(timestamp, errorResponse.getTimestamp());
        assertEquals(status, errorResponse.getStatus());
        assertEquals(error, errorResponse.getError());
        assertEquals(message, errorResponse.getMessage());
    }
}