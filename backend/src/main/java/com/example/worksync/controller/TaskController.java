package com.example.worksync.controller;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import com.example.worksync.dto.requests.TaskDTO;
import com.example.worksync.exceptions.NotFoundException;
import com.example.worksync.model.User;
import com.example.worksync.repository.TaskRepository;
import com.example.worksync.service.TaskService;


@RestController
@RequestMapping("/tasks")
public class TaskController {
   
    private final TaskService taskService;
    private final TaskRepository taskRepository;


    public TaskController(TaskService taskService, TaskRepository taskRepository) {
        this.taskService = taskService;
        this.taskRepository = taskRepository;
    }


    @GetMapping("/projects/{projectId}")
    public ResponseEntity<List<TaskDTO>> listTasksByProject(@PathVariable Long projectId) {
        List<TaskDTO> tasks = taskService.listTasksByProject(projectId);
        return ResponseEntity.ok(tasks);
    }


    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable Long id) {
        Optional<TaskDTO> task = taskService.findById(id);
        return task.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@RequestBody TaskDTO taskDTO, @AuthenticationPrincipal User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }
        if (taskDTO.getTitle() == null || taskDTO.getTitle().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        TaskDTO newTask = taskService.createTask(taskDTO, user);
        return ResponseEntity.ok(newTask);
    }


    @PatchMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable Long id, @RequestBody TaskDTO taskDTO) {
        TaskDTO updatedTask = taskService.updateTask(id, taskDTO);
        return ResponseEntity.ok(updatedTask);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        if (!taskRepository.existsById(id)) {
            throw new NotFoundException("Task not found!");
        }
        taskRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
   
   


    @GetMapping("/search")
    public ResponseEntity<?> searchTasks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) LocalDate startDateMin,
            @RequestParam(required = false) Long creatorId,
            @RequestParam(required = false) Long assignedPersonId,
            @RequestParam(required = false) LocalDate startDateMax) {
   
        try {
            String normalizedTitle = (title != null) ? title.trim().toLowerCase() : null;
   
            List<TaskDTO> tasks = taskService.searchTasks(normalizedTitle, startDateMin, startDateMax, creatorId, assignedPersonId);
   
            if (tasks.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
   
            return ResponseEntity.ok(tasks);
   
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching tasks: " + e.getMessage());
        }
    }
}
