package com.example.worksync.dto.requests;

import java.time.LocalDate;

import com.example.worksync.model.enums.TaskStatus;

public class TaskDTO {

    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private LocalDate startDate;
    private LocalDate completionDate;
    private LocalDate deadline;
    private Long responsibleId;
    private Long projectId;
    private String projectName; 

    public TaskDTO() {
    }

    public TaskDTO(Long id, String title, String description, TaskStatus status, 
                   LocalDate startDate, LocalDate completionDate, LocalDate deadline, 
                   Long responsibleId, Long projectId, String projectName) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.startDate = startDate;
        this.completionDate = completionDate;
        this.deadline = deadline;
        this.responsibleId = responsibleId;
        this.projectId = projectId;
        this.projectName = projectName; 
    }

    public TaskDTO(Long id, String title, String description, TaskStatus status, 
                   LocalDate startDate, LocalDate completionDate, LocalDate deadline, 
                   Long responsibleId, Long projectId) {
        this(id, title, description, status, startDate, completionDate, deadline, responsibleId, projectId, null);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getCompletionDate() { return completionDate; }
    public void setCompletionDate(LocalDate completionDate) { this.completionDate = completionDate; }

    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    public Long getResponsibleId() { return responsibleId; }
    public void setResponsibleId(Long responsibleId) { this.responsibleId = responsibleId; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
}