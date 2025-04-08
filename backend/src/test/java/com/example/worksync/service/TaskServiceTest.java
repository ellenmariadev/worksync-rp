package com.example.worksync.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.example.worksync.dto.requests.TaskDTO;
import com.example.worksync.event.UserTaskAssignmentEvent;
import com.example.worksync.exceptions.NotFoundException;
import com.example.worksync.model.Project;
import com.example.worksync.model.Task;
import com.example.worksync.model.User;
import com.example.worksync.model.enums.TaskStatus;
import com.example.worksync.repository.ProjectRepository;
import com.example.worksync.repository.TaskRepository;
import com.example.worksync.repository.UserRepository;

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
    private User newUser;
    private Project project;
    private Project newProject;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        newUser = new User();
        newUser.setId(2L);
        newUser.setEmail("new@example.com");

        project = new Project();
        project.setId(1L);
        project.setTitle("Project Test");

        newProject = new Project();
        newProject.setId(2L);
        newProject.setTitle("New Project");

        task = new Task();
        task.setId(1L);
        task.setTitle("Task Test");
        task.setDescription("Description");
        task.setProject(project);
        task.setAssignedPerson(user);
        task.setCreator(user);
        task.setStatus(TaskStatus.NOT_STARTED);
        task.setStartDate(LocalDate.now());
        task.setCompletionDate(null);
        task.setDeadline(LocalDate.now().plusDays(7));

        taskDTO = new TaskDTO(
                1L,
                "Task Test",
                "Description",
                TaskStatus.NOT_STARTED,
                LocalDate.now(),
                null,
                LocalDate.now().plusDays(7),
                user.getId(),
                user.getId(),
                project.getId(),
                project.getTitle());
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
    void testFindById_ShouldReturnEmpty_WhenTaskDoesNotExist() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<TaskDTO> result = taskService.findById(99L);

        assertFalse(result.isPresent());
    }

    @Test
    void testCreateTask_ShouldReturnTaskDTO_WhenValidData() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task savedTask = invocation.getArgument(0);
            savedTask.setId(1L);
            return savedTask;
        });

        TaskDTO result = taskService.createTask(taskDTO, user);

        assertNotNull(result);
        assertEquals("Task Test", result.getTitle());
        verify(eventPublisher, times(1)).publishEvent(any(UserTaskAssignmentEvent.class));
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

        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));

        taskDTO.setTitle("Updated Title");
        taskDTO.setStatus(TaskStatus.IN_PROGRESS);
        taskDTO.setResponsibleId(user.getId());
        taskDTO.setProjectId(project.getId());

        TaskDTO result = taskService.updateTask(1L, taskDTO);

        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        assertEquals(TaskStatus.IN_PROGRESS, result.getStatus());

        verify(taskRepository, times(1)).save(existingTask);
    }

    @Test
    void testUpdateTask_ShouldChangeAllFields_WhenAllFieldsProvided() {
        Task existingTask = new Task();
        existingTask.setId(1L);
        existingTask.setAssignedPerson(user);
        existingTask.setProject(project);
        existingTask.setTitle("Old Title");
        existingTask.setDescription("Old Description");
        existingTask.setStatus(TaskStatus.NOT_STARTED);
        existingTask.setStartDate(LocalDate.now().minusDays(5));
        existingTask.setCompletionDate(null);
        existingTask.setDeadline(LocalDate.now().plusDays(5));

        LocalDate newStartDate = LocalDate.now();
        LocalDate newCompletionDate = LocalDate.now().plusDays(2);
        LocalDate newDeadline = LocalDate.now().plusDays(10);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(userRepository.findById(newUser.getId())).thenReturn(Optional.of(newUser));
        when(projectRepository.findById(newProject.getId())).thenReturn(Optional.of(newProject));
        when(taskRepository.save(any(Task.class))).thenReturn(existingTask);

        TaskDTO updateDTO = new TaskDTO();
        updateDTO.setTitle("New Title");
        updateDTO.setDescription("New Description");
        updateDTO.setStatus(TaskStatus.DONE);
        updateDTO.setStartDate(newStartDate);
        updateDTO.setCompletionDate(newCompletionDate);
        updateDTO.setDeadline(newDeadline);
        updateDTO.setResponsibleId(newUser.getId());
        updateDTO.setProjectId(newProject.getId());

        taskService.updateTask(1L, updateDTO);

        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(taskCaptor.capture());

        Task capturedTask = taskCaptor.getValue();
        assertEquals("New Title", capturedTask.getTitle());
        assertEquals("New Description", capturedTask.getDescription());
        assertEquals(TaskStatus.DONE, capturedTask.getStatus());
        assertEquals(newStartDate, capturedTask.getStartDate());
        assertEquals(newCompletionDate, capturedTask.getCompletionDate());
        assertEquals(newDeadline, capturedTask.getDeadline());
        assertEquals(newUser, capturedTask.getAssignedPerson());
        assertEquals(newProject, capturedTask.getProject());
        verify(eventPublisher, times(1)).publishEvent(any(UserTaskAssignmentEvent.class));
    }

    @Test
    void testUpdateTask_ShouldOnlyUpdateNonNullFields() {
        Task existingTask = new Task();
        existingTask.setId(1L);
        existingTask.setTitle("Original Title");
        existingTask.setDescription("Original Description");
        existingTask.setStatus(TaskStatus.NOT_STARTED);
        existingTask.setStartDate(LocalDate.now().minusDays(5));
        existingTask.setCompletionDate(null);
        existingTask.setDeadline(LocalDate.now().plusDays(5));
        existingTask.setAssignedPerson(user);
        existingTask.setProject(project);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenReturn(existingTask);

        TaskDTO updateDTO = new TaskDTO();
        updateDTO.setTitle("Updated Title");

        taskService.updateTask(1L, updateDTO);

        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(taskCaptor.capture());

        Task capturedTask = taskCaptor.getValue();
        assertEquals("Updated Title", capturedTask.getTitle());
        assertEquals("Original Description", capturedTask.getDescription());
        assertEquals(TaskStatus.NOT_STARTED, capturedTask.getStatus());

        verify(eventPublisher, never()).publishEvent(any(UserTaskAssignmentEvent.class));
    }

    @Test
    void testUpdateTask_ShouldNotPublishEvent_WhenAssignedPersonNotChanged() {
        Task existingTask = new Task();
        existingTask.setId(1L);
        existingTask.setAssignedPerson(user);
        existingTask.setProject(project);
        existingTask.setTitle("Old Title");

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Task.class))).thenReturn(existingTask);

        TaskDTO updateDTO = new TaskDTO();
        updateDTO.setTitle("New Title");
        updateDTO.setResponsibleId(user.getId());

        taskService.updateTask(1L, updateDTO);

        verify(eventPublisher, never()).publishEvent(any(UserTaskAssignmentEvent.class));
    }

    @Test
    void testUpdateTask_ShouldHandleTitleField_BothNullAndNonNull() {
        Task existingTask = new Task();
        existingTask.setId(1L);
        existingTask.setTitle("Original Title");
        existingTask.setDescription("Original Description");
        existingTask.setStatus(TaskStatus.NOT_STARTED);
        existingTask.setAssignedPerson(user);
        existingTask.setProject(project);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArgument(0));

        TaskDTO updateDtoWithTitle = new TaskDTO();
        updateDtoWithTitle.setTitle("Updated Title");

        taskService.updateTask(1L, updateDtoWithTitle);

        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(taskCaptor.capture());

        Task capturedTask = taskCaptor.getValue();
        assertEquals("Updated Title", capturedTask.getTitle());

        Task secondTask = new Task();
        secondTask.setId(1L);
        secondTask.setTitle("Original Title");
        secondTask.setDescription("Original Description");
        secondTask.setStatus(TaskStatus.NOT_STARTED);
        secondTask.setAssignedPerson(user);
        secondTask.setProject(project);

        reset(taskRepository);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(secondTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArgument(0));
        TaskDTO updateDtoWithoutTitle = new TaskDTO();

        updateDtoWithoutTitle.setDescription("Updated Description");

        taskService.updateTask(1L, updateDtoWithoutTitle);

        taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(taskCaptor.capture());

        capturedTask = taskCaptor.getValue();
        assertEquals("Original Title", capturedTask.getTitle());
        assertEquals("Updated Description", capturedTask.getDescription());
    }

    @Test
    void testUpdateTask_ShouldThrowNotFoundException_WhenTaskNotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        TaskDTO updateDTO = new TaskDTO();
        updateDTO.setTitle("New Title");

        assertThrows(NotFoundException.class, () -> {
            taskService.updateTask(99L, updateDTO);
        });
    }

    @Test
    void testUpdateTask_ShouldThrowNotFoundException_WhenResponsiblePersonNotFound() {
        Task existingTask = new Task();
        existingTask.setId(1L);
        existingTask.setAssignedPerson(user);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        TaskDTO updateDTO = new TaskDTO();
        updateDTO.setResponsibleId(99L);

        assertThrows(NotFoundException.class, () -> {
            taskService.updateTask(1L, updateDTO);
        });
    }

    @Test
    void testUpdateTask_ShouldThrowNotFoundException_WhenProjectNotFound() {
        Task existingTask = new Task();
        existingTask.setId(1L);
        existingTask.setProject(project);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        TaskDTO updateDTO = new TaskDTO();
        updateDTO.setProjectId(99L);

        assertThrows(NotFoundException.class, () -> {
            taskService.updateTask(1L, updateDTO);
        });
    }

    @Test
    void testDeleteTask_ShouldDeleteTask_WhenTaskExists() {
        when(taskRepository.existsById(1L)).thenReturn(true);

        taskService.deleteTask(1L);

        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteTask_ShouldThrowNotFoundException_WhenTaskDoesNotExist() {
        when(taskRepository.existsById(99L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> {
            taskService.deleteTask(99L);
        });
    }

    @Test
    void testSearchTasks_ShouldPrioritizeParameters_WhenMultipleParametersProvided() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(1);

        when(taskRepository.findByTitleContainingIgnoreCase("Test")).thenReturn(List.of(task));

        List<TaskDTO> result = taskService.searchTasks("Test", startDate, endDate, 1L, 2L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Task Test", result.get(0).getTitle());

        verify(taskRepository, times(1)).findByTitleContainingIgnoreCase("Test");
        verify(taskRepository, never()).findByStartDateBetween(any(), any());
        verify(taskRepository, never()).findByCreatorId(any());
        verify(taskRepository, never()).findByAssignedPersonId(any());

        when(taskRepository.findByStartDateBetween(startDate, endDate)).thenReturn(List.of(task));

        result = taskService.searchTasks(null, startDate, endDate, 1L, 2L);

        verify(taskRepository, times(1)).findByStartDateBetween(startDate, endDate);
        verify(taskRepository, never()).findByCreatorId(any());
        verify(taskRepository, never()).findByAssignedPersonId(any());

        when(taskRepository.findByCreatorId(1L)).thenReturn(List.of(task));

        result = taskService.searchTasks(null, null, null, 1L, 2L);

        verify(taskRepository, times(1)).findByCreatorId(1L);
        verify(taskRepository, never()).findByAssignedPersonId(any());
    }

    @Test
    void testSearchTasks_ShouldHandleDateParameters_WithPartialNulls() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(1);

        when(taskRepository.findByStartDateBetween(startDate, endDate)).thenReturn(List.of(task));
        List<TaskDTO> result = taskService.searchTasks(null, startDate, endDate, null, null);
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(taskRepository).findByStartDateBetween(startDate, endDate);

        when(taskRepository.findByCreatorId(1L)).thenReturn(List.of(task));
        result = taskService.searchTasks(null, null, endDate, 1L, null);
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(taskRepository).findByCreatorId(1L);

        result = taskService.searchTasks(null, startDate, null, 1L, null);
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(taskRepository, times(2)).findByCreatorId(1L);

        verify(taskRepository, never()).findByTitleContainingIgnoreCase(any());
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
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(1);

        when(taskRepository.findByStartDateBetween(startDate, endDate))
                .thenReturn(List.of(task));

        List<TaskDTO> result = taskService.searchTasks(null, startDate, endDate, null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testSearchTasks_ShouldReturnTasks_WhenCreatorIdMatches() {
        when(taskRepository.findByCreatorId(1L)).thenReturn(List.of(task));

        List<TaskDTO> result = taskService.searchTasks(null, null, null, 1L, null);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testSearchTasks_ShouldReturnTasks_WhenAssignedPersonIdMatches() {
        when(taskRepository.findByAssignedPersonId(1L)).thenReturn(List.of(task));

        List<TaskDTO> result = taskService.searchTasks(null, null, null, null, 1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testSearchTasks_ShouldThrowException_WhenNoValidParamsProvided() {
        assertThrows(IllegalArgumentException.class, () -> {
            taskService.searchTasks(null, null, null, null, null);
        });
    }

    @Test
    void testConvertToDTO_ShouldHandleNullValues() {
        Task taskWithNulls = new Task();
        taskWithNulls.setId(1L);
        taskWithNulls.setTitle("Test Task");

        TaskDTO result = taskService.findById(1L).orElse(null);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(taskWithNulls));
        result = taskService.findById(1L).orElse(null);

        assertNotNull(result);
        assertEquals("Test Task", result.getTitle());
        assertNull(result.getDescription());
        assertNull(result.getStatus());
        assertNull(result.getStartDate());
        assertNull(result.getCompletionDate());
        assertNull(result.getDeadline());
        assertNull(result.getResponsibleId());
        assertNull(result.getCreatorId());
        assertNull(result.getProjectId());
        assertNull(result.getProjectName());
    }

    @Test
    void testConvertToEntity_ShouldThrowNotFoundException_WhenResponsiblePersonNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        TaskDTO dto = new TaskDTO();
        dto.setResponsibleId(99L);

        assertThrows(NotFoundException.class, () -> {
            taskService.createTask(dto, user);
        });
    }

    @Test
    void testConvertToEntity_ShouldThrowNotFoundException_WhenProjectNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        TaskDTO dto = new TaskDTO();
        dto.setResponsibleId(1L);
        dto.setProjectId(99L);

        assertThrows(NotFoundException.class, () -> {
            taskService.createTask(dto, user);
        });
    }
}