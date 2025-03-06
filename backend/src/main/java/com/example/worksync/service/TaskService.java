package com.example.worksync.service;

import com.example.worksync.dto.requests.TaskDTO;
import com.example.worksync.exceptions.NotFoundException;
import com.example.worksync.model.Project;
import com.example.worksync.model.Task;
import com.example.worksync.model.User;
import com.example.worksync.repository.ProjectRepository;
import com.example.worksync.repository.TaskRepository;
import com.example.worksync.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        if (!taskRepository.existsById(id)) {
            throw new NotFoundException("Task not found!");
        }
        Task task = convertToEntity(dto);
        task.setId(id);
        task = taskRepository.save(task);
        return convertToDTO(task);
    }

    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new NotFoundException("Task not found!");
        }
        taskRepository.deleteById(id);
    }

    public List<TaskDTO> searchTasks(String titulo, LocalDate dataInicioMin, LocalDate dataInicioMax) {
        List<Task> tasks;

        if (titulo != null) {
            tasks = taskRepository.findByTitleContainingIgnoreCase(titulo);
        } else if (dataInicioMin != null && dataInicioMax != null) {
            tasks = taskRepository.findByStartDateBetween(dataInicioMin, dataInicioMax);
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
            task.getProject() != null ? task.getProject().getId() : null
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
