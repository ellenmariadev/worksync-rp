package com.example.worksync.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TaskStatus {
    NOT_STARTED,
    IN_PROGRESS,
    DONE;

    @JsonCreator
    public static TaskStatus fromString(String status) {
        if (status != null) {
            for (TaskStatus ts : TaskStatus.values()) {
                // Ignora maiúsculas/minúsculas ao comparar a string com os valores do enum
                if (status.equalsIgnoreCase(ts.name())) {
                    return ts;
                }
            }
        }
        throw new IllegalArgumentException("Unknown status: " + status);
    }

    @JsonValue
    public String toJsonValue() {
        return name();  // Retorna o nome do enum como string
    }
}
