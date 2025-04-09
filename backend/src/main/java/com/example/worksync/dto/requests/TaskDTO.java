package com.example.worksync.dto.requests;


import java.time.LocalDate;

import com.example.worksync.model.Task;
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
    private Long creatorId;
    private Long projectId;
    private String projectName;


    public TaskDTO() {
    }

    public TaskDTO(Task task) {
        if (task != null) {
            this.id = task.getId();
            this.title = task.getTitle();
            this.description = task.getDescription();
            this.status = task.getStatus();
            this.startDate = task.getStartDate();
            this.completionDate = task.getCompletionDate();
            this.deadline = task.getDeadline();
            
            if (task.getAssignedPerson() != null) {
                this.responsibleId = task.getAssignedPerson().getId();
            }
            
            if (task.getCreator() != null) {
                this.creatorId = task.getCreator().getId();
            }
            
            if (task.getProject() != null) {
                this.projectId = task.getProject().getId();
                this.projectName = task.getProject().getTitle();
            }
        }
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


    public Long getCreatorId() { return creatorId; }
    public void setCreator (Long creator) { this.creatorId = creator; }


    public Long getResponsibleId() { return responsibleId; }
    public void setResponsibleId(Long responsibleId) { this.responsibleId = responsibleId; }


    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }


    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
}
