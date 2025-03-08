package com.example.worksync.dto.requests;

import java.util.List;

public class ProjectDTO {
    private Long id;
    private String title;
    private String description;
    private List<Long> participantIds;
    private List<Long> taskIds;

    public ProjectDTO(Long id, String title, String description, List<Long> participantIds, List<Long> taskIds) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.participantIds = participantIds;
        this.taskIds = taskIds;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<Long> getParticipantIds() { return participantIds; }
    public void setParticipantIds(List<Long> participantIds) { this.participantIds = participantIds; }

    public List<Long> getTaskIds() { return taskIds; }
    public void setTaskIds(List<Long> taskIds) { this.taskIds = taskIds; }
}
