package com.example.worksync.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.worksync.dto.requests.TaskDTO;
import com.example.worksync.exceptions.NotFoundException;
import com.example.worksync.model.Project;
import com.example.worksync.model.Task;
import com.example.worksync.model.User;
import com.example.worksync.repository.ProjectRepository;
import com.example.worksync.repository.TaskRepository;
import com.example.worksync.repository.UserRepository;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;
    
    public List<TaskDTO> listTasksByProject(Long projectId) {
        List<Task> tasks = taskRepository.findByProjectId(projectId);
        return tasks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<TaskDTO> findById(Long id) {
        return taskRepository.findById(id).map(this::convertToDTO);
    }

    public TaskDTO createTask(TaskDTO dto) {
        Task task = convertToEntity(dto);
        task = taskRepository.save(task);
        return convertToDTO(task);
    }

    public TaskDTO updateTask(Long id, TaskDTO dto) {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Task not found!"));

        if (dto.getResponsibleId() != null) {
            User responsiblePerson = userRepository.findById(dto.getResponsibleId())
                    .orElseThrow(() -> new NotFoundException("Responsible person not found!"));
            existingTask.setAssignedPerson(responsiblePerson);
        }

        if (dto.getProjectId() != null) {
            Project project = projectRepository.findById(dto.getProjectId())
                    .orElseThrow(() -> new NotFoundException("Project not found!"));
            existingTask.setProject(project);
        }

        if (dto.getTitle() != null) {
            existingTask.setTitle(dto.getTitle());
        }

        if (dto.getDescription() != null) {
            existingTask.setDescription(dto.getDescription());
        }

        if (dto.getStatus() != null) {
            existingTask.setStatus(dto.getStatus());
        }

        if (dto.getStartDate() != null) {
            existingTask.setStartDate(dto.getStartDate());
        }

        if (dto.getCompletionDate() != null) {
            existingTask.setCompletionDate(dto.getCompletionDate());
        }

        if (dto.getDeadline() != null) {
            existingTask.setDeadline(dto.getDeadline());
        }

        existingTask = taskRepository.save(existingTask);
        return convertToDTO(existingTask);
    }
    
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new NotFoundException("Task not found!");
        }
        taskRepository.deleteById(id);
    }
    
    public List<TaskDTO> searchTasks(String title, LocalDate startDateMin, LocalDate startDateMax) {
        List<Task> tasks;
    
        if (title != null) {
            tasks = taskRepository.findByTitleContainingIgnoreCase(title);
        } else if (startDateMin != null && startDateMax != null) {
            tasks = taskRepository.findByStartDateBetween(startDateMin, startDateMax);
        } else {
            throw new IllegalArgumentException("Provide either title or a valid date range.");
        }
    
        return tasks.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private TaskDTO convertToDTO(Task task) {
        return new TaskDTO(
            task.getId(),
            task.getTitle(),
            task.getDescription(),
            task.getStatus(),
            task.getStartDate(),
            task.getCompletionDate(),
            task.getDeadline(),
            task.getAssignedPerson() != null ? task.getAssignedPerson().getId() : null,
            task.getProject() != null ? task.getProject().getId() : null,
            task.getProject() != null ? task.getProject().getTitle() : null
        );
    }

    private Task convertToEntity(TaskDTO dto) {
        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setStatus(dto.getStatus());
        task.setStartDate(dto.getStartDate());
        task.setCompletionDate(dto.getCompletionDate());
        task.setDeadline(dto.getDeadline());

        User responsiblePerson = userRepository.findById(dto.getResponsibleId())
                .orElseThrow(() -> new NotFoundException("Responsible person not found!"));
        task.setAssignedPerson(responsiblePerson);

        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new NotFoundException("Project not found!"));
        task.setProject(project);
        
        return task;
    }
}
