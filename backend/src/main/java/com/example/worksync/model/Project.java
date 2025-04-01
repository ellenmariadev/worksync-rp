package com.example.worksync.model;

import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    @ElementCollection
    private List<Long> participantIds;

    @ElementCollection
    private List<Long> taskIds;

    public Project() {}

    public Project(String title, String description, List<Long> participantIds, List<Long> taskIds) {
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

    public List<Long> getParticipantIds() { 
        return participantIds; 
    }

    public void setParticipantIds(List<Long> participantIds) { this.participantIds = participantIds; }

    public List<Long> getTaskIds() { return taskIds; }
    public void setTaskIds(List<Long> taskIds) { this.taskIds = taskIds; }
}