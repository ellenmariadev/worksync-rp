package com.example.worksync.model;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

import com.example.worksync.model.enums.TaskStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    private LocalDate startDate;
    private LocalDate completionDate;
    private LocalDate deadline;

    @ManyToOne
    @JoinColumn(name = "assigned_person_id", nullable = false)
    private User assignedPerson;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
    
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    public Task() {}

    public Task(String title, String description, TaskStatus status, LocalDate startDate, LocalDate completionDate, LocalDate deadline, User assignedPerson, Project project) {
        this.comments = new ArrayList<>();
        this.title = title;
        this.description = description;
        this.status = status;
        this.startDate = startDate;
        this.completionDate = completionDate;
        this.deadline = deadline;
        this.assignedPerson = assignedPerson;
        this.project = project;
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

    public User getAssignedPerson() { return assignedPerson; }
    public void setAssignedPerson(User assignedPerson) { this.assignedPerson = assignedPerson; }

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }

    public List<Comment> getComments() { return comments; }
    public void setComments(List<Comment> comments) { this.comments = comments; }
}
