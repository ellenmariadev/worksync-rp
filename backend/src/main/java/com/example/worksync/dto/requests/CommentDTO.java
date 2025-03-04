package com.example.worksync.dto.requests;

import java.time.LocalDateTime;

public class CommentDTO {
    private Long id;
    private String description;
    private Long taskId;
    private Long userId;
    private LocalDateTime createdAt;

    public CommentDTO(Long id, String description, Long taskId, Long userId, LocalDateTime createdAt) {
        this.id = id;
        this.description = description;
        this.taskId = taskId;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
