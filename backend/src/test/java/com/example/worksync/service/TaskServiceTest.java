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
import org.springframework.context.ApplicationEventPublisher;

import com.example.worksync.dto.requests.TaskDTO;
import com.example.worksync.exceptions.NotFoundException;
import com.example.worksync.model.Project;
import com.example.worksync.model.Task;
import com.example.worksync.model.User;
import com.example.worksync.model.enums.TaskStatus;
import com.example.worksync.repository.ProjectRepository;
import com.example.worksync.repository.TaskRepository;
import com.example.worksync.repository.UserRepository;
import com.example.worksync.event.UserTaskAssignmentEvent;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

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
    void testFindById_ShouldThrowNotFoundException_WhenTaskDoesNotExist() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> taskService.findById(1L).orElseThrow(() -> new NotFoundException("Task not found!")));
    }
            
    @Test
    void testCreateTask_ShouldThrowNotFoundException_WhenResponsiblePersonNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> taskService.createTask(taskDTO, user));
    }

    @Test
    void testUpdateTask_ShouldReturnUpdatedTaskDTO_WhenValidData() {
        // Criar uma tarefa existente para atualizar
        Task existingTask = new Task();
        existingTask.setId(1L);
        existingTask.setAssignedPerson(user);  // Definindo o responsável
        existingTask.setProject(project);      // Definindo o projeto
        existingTask.setTitle("Old Title");   // Título original
        existingTask.setStatus(TaskStatus.NOT_STARTED);  // Status original
    
        // Mock para encontrar a tarefa existente
        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
    
        // Mock para salvar a tarefa (deve retornar a tarefa com os dados atualizados)
        when(taskRepository.save(any(Task.class))).thenReturn(existingTask);
    
        // Mock para encontrar o responsável (usuário com ID 1L)
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
    
        // Atualiza o DTO com novos dados
        taskDTO.setTitle("Updated Title");  // Novo título
        taskDTO.setStatus(TaskStatus.IN_PROGRESS);  // Novo status
        taskDTO.setResponsibleId(user.getId());  // Garantindo que o responsibleId não seja nulo
    
        // Chama o método de atualização
        TaskDTO result = taskService.updateTask(1L, taskDTO);
    
        // Verificação dos resultados
        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        assertEquals(TaskStatus.IN_PROGRESS, result.getStatus());
    
        // Verifica se o método save foi chamado corretamente
        verify(taskRepository, times(1)).save(existingTask);
    }

    @Test
    void testUpdateTask_ShouldThrowNotFoundException_WhenTaskNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> taskService.updateTask(1L, taskDTO));
    }

    @Test
    void testDeleteTask_ShouldDeleteTask_WhenTaskExists() {
        when(taskRepository.existsById(1L)).thenReturn(true);

        taskService.deleteTask(1L);

        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteTask_ShouldThrowNotFoundException_WhenTaskDoesNotExist() {
        when(taskRepository.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> taskService.deleteTask(1L));
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

    @Test
    void testSearchTasks_ShouldThrowIllegalArgumentException_WhenNoCriteriaProvided() {
        assertThrows(IllegalArgumentException.class, () -> taskService.searchTasks(null, null, null, null, null));
    }

    @Test
    void testCreateTask_ShouldPublishEvent_WhenTaskCreated() {
        // Criação de uma TaskDTO com projectId válido
        taskDTO.setProjectId(project.getId());  // Certifique-se de que o projectId não seja null
        
        // Mock para salvar a tarefa
        when(taskRepository.save(any(Task.class))).thenReturn(task);
    
        // Mock para encontrar o usuário com ID válido
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
    
        // Mock para encontrar o projeto com ID válido
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
    
        // Chamada para o método que está sendo testado
        TaskDTO result = taskService.createTask(taskDTO, user);
    
        // Verifique se o evento foi publicado uma vez
        verify(eventPublisher, times(1)).publishEvent(any(UserTaskAssignmentEvent.class));
    }    
}
