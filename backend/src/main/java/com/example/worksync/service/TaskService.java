package com.example.worksync.service;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;


import com.example.worksync.dto.requests.TaskDTO;
import com.example.worksync.event.UserTaskAssignmentEvent;
import com.example.worksync.exceptions.NotFoundException;
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


    public TaskService(TaskRepository taskRepository, ProjectRepository projectRepository,
            UserRepository userRepository, ApplicationEventPublisher eventPublisher) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }


    public List<TaskDTO> listTasksByProject(Long projectId) {
        List<Task> tasks = taskRepository.findByProjectId(projectId);
        return tasks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    public Optional<TaskDTO> findById(Long id) {
        return taskRepository.findById(id).map(this::convertToDTO);
    }


    public TaskDTO createTask(TaskDTO dto, User user) {
        Task task = convertToEntity(dto, user);
        task = taskRepository.save(task);
        eventPublisher.publishEvent(new UserTaskAssignmentEvent(this, task.getAssignedPerson(), task));
        return convertToDTO(task);
    }


    public TaskDTO updateTask(Long id, TaskDTO dto) {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Task not found!"));


        User previousAssignedPerson = existingTask.getAssignedPerson();
        boolean assignedPersonChanged = false;


        if (dto.getResponsibleId() != null) {
            User responsiblePerson = userRepository.findById(dto.getResponsibleId())
                    .orElseThrow(() -> new NotFoundException("Responsible person not found!"));
            if (!responsiblePerson.equals(previousAssignedPerson)) {
                existingTask.setAssignedPerson(responsiblePerson);
                assignedPersonChanged = true;
            }
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


        if (assignedPersonChanged) {
            eventPublisher
                    .publishEvent(new UserTaskAssignmentEvent(this, existingTask.getAssignedPerson(), existingTask));
        }


        return convertToDTO(existingTask);
    }


    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new NotFoundException("Task not found!");
        }
        taskRepository.deleteById(id);
    }


    public List<TaskDTO> searchTasks(String title, LocalDate startDateMin, LocalDate startDateMax, Long creatorId, Long assignedPersonId) {
        List<Task> tasks;


        if (title != null) {
            tasks = taskRepository.findByTitleContainingIgnoreCase(title);
        } else if (startDateMin != null && startDateMax != null) {
            tasks = taskRepository.findByStartDateBetween(startDateMin, startDateMax);
        } else if (creatorId != null) {
            tasks = taskRepository.findByCreatorId(creatorId);
        } else if (assignedPersonId != null) {
            tasks = taskRepository.findByAssignedPersonId(assignedPersonId);
        } else {
            throw new IllegalArgumentException("Provide valid params.");
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
                task.getCreator() != null ? task.getCreator().getId() : null,
                task.getProject() != null ? task.getProject().getId() : null,
                task.getProject() != null ? task.getProject().getTitle() : null);
    }


    private Task convertToEntity(TaskDTO dto, User user) {
        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setStatus(dto.getStatus());
        task.setStartDate(dto.getStartDate());
        task.setCompletionDate(dto.getCompletionDate());
        task.setDeadline(dto.getDeadline());
        task.setCreator(user);


        User responsiblePerson = userRepository.findById(dto.getResponsibleId())
                .orElseThrow(() -> new NotFoundException("Responsible person not found!"));
        task.setAssignedPerson(responsiblePerson);


        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new NotFoundException("Project not found!"));
        task.setProject(project);


        return task;
    }
}
