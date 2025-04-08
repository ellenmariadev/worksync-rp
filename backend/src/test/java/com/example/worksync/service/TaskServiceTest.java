package com.example.worksync.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.worksync.dto.requests.TaskDTO;
import com.example.worksync.model.Project;
import com.example.worksync.model.Task;
import com.example.worksync.model.User;
import com.example.worksync.model.enums.TaskStatus;
import com.example.worksync.repository.TaskRepository;
import com.example.worksync.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    private Task task;
    private TaskDTO taskDTO;
    private User user;
    private Project project;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        project = new Project();
        project.setId(1L);
        project.setTitle("Project Test");

        task = new Task();
        task.setId(1L);
        task.setTitle("Task Test");
        task.setProject(project);
        task.setAssignedPerson(user);

        taskDTO = new TaskDTO(1L, "Task Test", "Description", TaskStatus.NOT_STARTED, LocalDate.now(), null, null, user.getId(), project.getId(), null);
    }

    @Test
    void testListTasksByProject_ShouldReturnList_WhenTasksExist() {
        when(taskRepository.findByProjectId(1L)).thenReturn(List.of(task));

        List<TaskDTO> result = taskService.listTasksByProject(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Task Test", result.get(0).getTitle());
    }

    @Test
    void testListTasksByProject_ShouldReturnEmptyList_WhenNoTasksFound() {
        when(taskRepository.findByProjectId(1L)).thenReturn(List.of());

        List<TaskDTO> result = taskService.listTasksByProject(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindById_ShouldReturnTaskDTO_WhenTaskExists() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        Optional<TaskDTO> result = taskService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Task Test", result.get().getTitle());
    }

    @Test
    void testUpdateTask_ShouldReturnUpdatedTaskDTO_WhenValidData() {
        Task existingTask = new Task();
        existingTask.setId(1L);
        existingTask.setAssignedPerson(user);  
        existingTask.setProject(project);      
        existingTask.setTitle("Old Title");   
        existingTask.setStatus(TaskStatus.NOT_STARTED);  
    
        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));

        when(taskRepository.save(any(Task.class))).thenReturn(existingTask);
    
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
    
        taskDTO.setTitle("Updated Title"); 
        taskDTO.setStatus(TaskStatus.IN_PROGRESS);  
        taskDTO.setResponsibleId(user.getId());  
    
        TaskDTO result = taskService.updateTask(1L, taskDTO);

        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        assertEquals(TaskStatus.IN_PROGRESS, result.getStatus());
    
        verify(taskRepository, times(1)).save(existingTask);
    }

    @Test
    void testDeleteTask_ShouldDeleteTask_WhenTaskExists() {
        when(taskRepository.existsById(1L)).thenReturn(true);

        taskService.deleteTask(1L);

        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    void testSearchTasks_ShouldReturnTasks_WhenTitleMatches() {
        when(taskRepository.findByTitleContainingIgnoreCase("Test")).thenReturn(List.of(task));

        List<TaskDTO> result = taskService.searchTasks("Test", null, null, null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Task Test", result.get(0).getTitle());
    }

    @Test
    void testSearchTasks_ShouldReturnTasks_WhenDateRangeMatches() {
        when(taskRepository.findByStartDateBetween(LocalDate.now(), LocalDate.now().plusDays(1)))
                .thenReturn(List.of(task));

        List<TaskDTO> result = taskService.searchTasks(null, LocalDate.now(), LocalDate.now().plusDays(1), null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

  
}