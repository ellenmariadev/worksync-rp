package com.example.worksync.exceptions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ConflictExceptionTest {

    @Test
    void constructor_shouldSetMessage() {
        String expectedMessage = "Resource already exists.";
        ConflictException exception = new ConflictException(expectedMessage);
        assertEquals(expectedMessage, exception.getMessage());
    }
}