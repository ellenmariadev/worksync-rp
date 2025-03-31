package com.example.worksync.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.worksync.dto.requests.TaskDTO;
import com.example.worksync.event.UserTaskAssignmentEvent;
import com.example.worksync.exceptions.NotFoundException;
import com.example.worksync.exceptions.UnauthorizedAccessException;
import com.example.worksync.model.Project;
import com.example.worksync.model.Task;
import com.example.worksync.model.User;
import com.example.worksync.repository.ProjectRepository;
import com.example.worksync.repository.TaskRepository;
import com.example.worksync.repository.UserRepository;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ProjectService projectService;

    public TaskService(TaskRepository taskRepository, ProjectRepository projectRepository,
            UserRepository userRepository, ApplicationEventPublisher eventPublisher, ProjectService projectService) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
        this.projectService = projectService;
    }

    public List<TaskDTO> listTasksByProject(Long projectId) {
        return taskRepository.findByProjectId(projectId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<TaskDTO> findById(Long id) {
        return taskRepository.findById(id).map(this::convertToDTO);
    }

    public TaskDTO createTask(TaskDTO dto) {
        Task task = convertToEntity(dto);
        task = taskRepository.save(task);
        eventPublisher.publishEvent(new UserTaskAssignmentEvent(this, task.getAssignedPerson(), task));
        return convertToDTO(task);
    }

    public TaskDTO updateTask(Long id, TaskDTO dto) {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Task not found!"));
        
        Project project = existingTask.getProject();
        User authenticatedUser = getAuthenticatedUser();
        Long userId = authenticatedUser.getId();
        boolean isAdmin = "ROLE_ADMIN".equals(authenticatedUser.getRole().name());
        
        if (project == null || (!isAdmin && !project.getParticipantIds().contains(userId))) {
            throw new UnauthorizedAccessException("User is not authorized to update the task status");
        }
    
        boolean assignedPersonChanged = false;
        
        if (dto.getResponsibleId() != null) {
            User responsiblePerson = userRepository.findById(dto.getResponsibleId())
                    .orElseThrow(() -> new NotFoundException("Responsible person not found!"));
            if (!responsiblePerson.equals(existingTask.getAssignedPerson())) {
                existingTask.setAssignedPerson(responsiblePerson);
                assignedPersonChanged = true;
            }
        }
        
        if (dto.getStatus() != null) {
            existingTask.setStatus(dto.getStatus());
        }
        
        existingTask = taskRepository.save(existingTask);
        
        if (assignedPersonChanged) {
            eventPublisher.publishEvent(new UserTaskAssignmentEvent(this, existingTask.getAssignedPerson(), existingTask));
        }
        
        return convertToDTO(existingTask);
    }
    
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new NotFoundException("Task not found!");
        }
        taskRepository.deleteById(id);
    }

    public List<TaskDTO> searchTasks(String title, LocalDate startDateMin, LocalDate startDateMax) {
        if (title != null) {
            return taskRepository.findByTitleContainingIgnoreCase(title).stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } else if (startDateMin != null && startDateMax != null) {
            return taskRepository.findByStartDateBetween(startDateMin, startDateMax).stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } else {
            throw new IllegalArgumentException("Provide either title or a valid date range.");
        }
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new UnauthorizedAccessException("No authenticated user found");
        }
    
        // Pegando o principal como UserDetails
        UserDetails userDetails = (UserDetails) authentication.getPrincipal(); // Aqui acessamos o usuário autenticado
    
        // Agora, busque o usuário pelo email (nome de usuário) no repositório
        return userRepository.findByEmail(userDetails.getUsername()) // Agora procuramos o email do usuário
                .orElseThrow(() -> new NotFoundException("Authenticated user not found"));
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
                task.getProject() != null ? task.getProject().getTitle() : null);
    }

    private Task convertToEntity(TaskDTO dto) {
        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setStatus(dto.getStatus());
        task.setStartDate(dto.getStartDate());
        task.setCompletionDate(dto.getCompletionDate());
        task.setDeadline(dto.getDeadline());
        task.setAssignedPerson(userRepository.findById(dto.getResponsibleId())
                .orElseThrow(() -> new NotFoundException("Responsible person not found!")));
        task.setProject(projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new NotFoundException("Project not found!")));
        return task;
    }
}
