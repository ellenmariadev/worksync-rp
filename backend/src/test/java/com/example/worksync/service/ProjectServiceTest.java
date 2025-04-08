package com.example.worksync.service;

import com.example.worksync.dto.requests.ProjectDTO;
import com.example.worksync.exceptions.NotFoundException;
import com.example.worksync.exceptions.UnauthorizedAccessException;
import com.example.worksync.model.Project;
import com.example.worksync.model.User;
import com.example.worksync.repository.ProjectRepository;
import com.example.worksync.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProjectService projectService;

    private Project project;
    private ProjectDTO projectDTO;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        project = new Project();
        project.setId(1L);
        project.setTitle("Test Project");
        project.setDescription("This is a test project");
        project.setParticipantIds(List.of(1L, 2L));
        project.setTaskIds(List.of(10L, 20L));

        projectDTO = new ProjectDTO(1L, "Test Project", "This is a test project", List.of(1L, 2L), List.of(10L, 20L));

        user = new User();
        user.setId(1L);
        user.setName("testuser");
    }

    @Test
    void testListProjects_Success() {
        when(projectRepository.findAll()).thenReturn(List.of(project));

        List<ProjectDTO> result = projectService.listProjects(null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Project", result.get(0).getTitle());
    }

    @Test
    void testListProjects_WithTitleFilter() {
        when(projectRepository.findByTitleContainingIgnoreCase("Test")).thenReturn(List.of(project));

        List<ProjectDTO> result = projectService.listProjects("Test");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Project", result.get(0).getTitle());
    }

    @Test
    void testFindById_Success() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        Optional<ProjectDTO> result = projectService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Test Project", result.get().getTitle());
    }

    @Test
    void testFindById_NotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<ProjectDTO> result = projectService.findById(1L);

        assertFalse(result.isPresent());
    }

    @Test
    void testCreateProject_Success() {
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        ProjectDTO result = projectService.createProject(projectDTO);

        assertNotNull(result);
        assertEquals("Test Project", result.getTitle());
    }

    @Test
    void testUpdateProject_Success() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        ProjectDTO updatedDTO = new ProjectDTO(null, "Updated Title", null, null, null);
        ProjectDTO result = projectService.updateProject(1L, updatedDTO);

        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
    }

    @Test
    void testUpdateProject_NotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> projectService.updateProject(1L, projectDTO));

        assertEquals("Project not found!", exception.getMessage());
    }

    @Test
    void testDeleteProject_Success() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        doNothing().when(projectRepository).deleteById(1L);

        assertDoesNotThrow(() -> projectService.deleteProject(1L));
        verify(projectRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteProject_NotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> projectService.deleteProject(1L));

        assertEquals("Project not found!", exception.getMessage());
    }

    @Test
    void testAddParticipantToProject_Success() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProjectDTO result = projectService.addParticipantToProject(1L, 3L);

        assertNotNull(result);
        assertTrue(result.getParticipantIds().contains(3L));
    }

    @Test
    void testAddParticipantToProject_AlreadyExists() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> projectService.addParticipantToProject(1L, 1L));

        assertEquals("User is already a participant of this project!", exception.getMessage());
    }

    @Test
    void testAddParticipantToProject_NotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> projectService.addParticipantToProject(1L, 3L));

        assertEquals("Project not found!", exception.getMessage());
    }

    @Test
    void testGetParticipants_Success() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User()));

        List<User> participants = projectService.getParticipants(1L, 1L);

        assertNotNull(participants);
        assertEquals(2, participants.size());
    }

    @Test
    void testGetParticipants_UnauthorizedAccess() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        UnauthorizedAccessException exception = assertThrows(UnauthorizedAccessException.class,
                () -> projectService.getParticipants(1L, 999L));

        assertEquals("User is not a participant of this project", exception.getMessage());
    }

    @Test
    void testGetParticipants_ProjectNotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> projectService.getParticipants(1L, 1L));

        assertEquals("Project not found", exception.getMessage());
    }
}
