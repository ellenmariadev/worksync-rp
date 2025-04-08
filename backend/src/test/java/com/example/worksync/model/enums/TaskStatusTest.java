package com.example.worksync.model.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskStatusTest {

    @Test
    void testFromString_ValidInputs() {
        assertEquals(TaskStatus.NOT_STARTED, TaskStatus.fromString("NOT_STARTED"));
        assertEquals(TaskStatus.IN_PROGRESS, TaskStatus.fromString("IN_PROGRESS"));
        assertEquals(TaskStatus.DONE, TaskStatus.fromString("DONE"));
    }

    @Test
    void testFromString_CaseInsensitive() {
        assertEquals(TaskStatus.NOT_STARTED, TaskStatus.fromString("not_started"));
        assertEquals(TaskStatus.IN_PROGRESS, TaskStatus.fromString("in_progress"));
        assertEquals(TaskStatus.DONE, TaskStatus.fromString("done"));
    }

    @Test
    void testFromString_InvalidInput() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> 
            TaskStatus.fromString("invalid_status")
        );
        assertEquals("Unknown status: invalid_status", exception.getMessage());
    }

    @Test
    void testFromString_NullInput() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            TaskStatus.fromString(null)
        );
        assertEquals("Unknown status: null", exception.getMessage());
    }

    @Test
    void testToJsonValue() {
        assertEquals("NOT_STARTED", TaskStatus.NOT_STARTED.toJsonValue());
        assertEquals("IN_PROGRESS", TaskStatus.IN_PROGRESS.toJsonValue());
        assertEquals("DONE", TaskStatus.DONE.toJsonValue());
    }
}
