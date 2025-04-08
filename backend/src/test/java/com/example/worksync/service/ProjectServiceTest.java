package com.example.worksync.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.example.worksync.dto.requests.ProjectDTO;
import com.example.worksync.exceptions.NotFoundException;
import com.example.worksync.exceptions.UnauthorizedAccessException;
import com.example.worksync.model.Project;
import com.example.worksync.model.User;
import com.example.worksync.repository.ProjectRepository;
import com.example.worksync.repository.UserRepository;

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
    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user1 = new User();
        user1.setId(1L);
        user1.setName("User One");

        user2 = new User();
        user2.setId(2L);
        user2.setName("User Two");

        project = new Project();
        project.setId(1L);
        project.setTitle("Test Project");
        project.setDescription("This is a test project");
        project.setParticipantIds(new ArrayList<>(Arrays.asList(1L, 2L)));
        project.setTaskIds(new ArrayList<>(Arrays.asList(10L, 20L)));

        projectDTO = new ProjectDTO(1L, "Test Project", "This is a test project", List.of(1L, 2L), List.of(10L, 20L));

        user = new User();
        user.setId(1L);
        user.setName("testuser");
    }

    @Test
    void testGetParticipants_WithMissingUsers() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());  // Um usuário não encontrado

        List<User> result = projectService.getParticipants(1L, 1L);

        assertNotNull(result);
        assertEquals(1, result.size());  // Apenas um usuário encontrado
        assertEquals("User One", result.get(0).getName());
    }

    @Test
    void testListProjects_WithoutTitle() {
        when(projectRepository.findAll()).thenReturn(List.of(project));

        List<ProjectDTO> result = projectService.listProjects(null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Project", result.get(0).getTitle());
        verify(projectRepository).findAll();
        verify(projectRepository, never()).findByTitleContainingIgnoreCase(any());
    }

    @Test
    void testListProjects_WithEmptyTitle() {
        when(projectRepository.findAll()).thenReturn(List.of(project));

        List<ProjectDTO> result = projectService.listProjects("");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(projectRepository).findAll();
        verify(projectRepository, never()).findByTitleContainingIgnoreCase(any());
    }

    @Test
    void testListProjects_WithTitle() {
        when(projectRepository.findByTitleContainingIgnoreCase("Test")).thenReturn(List.of(project));

        List<ProjectDTO> result = projectService.listProjects("Test");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Project", result.get(0).getTitle());
        verify(projectRepository).findByTitleContainingIgnoreCase("Test");
        verify(projectRepository, never()).findAll();
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
    void testCreateProject_WithAllFields() {
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        ProjectDTO result = projectService.createProject(projectDTO);

        assertNotNull(result);
        assertEquals("Test Project", result.getTitle());
        assertEquals("This is a test project", result.getDescription());
        assertEquals(2, result.getParticipantIds().size());
        assertEquals(2, result.getTaskIds().size());
    }

    @Test
    void testCreateProject_WithNullLists() {
    ProjectDTO dtoWithNullLists = new ProjectDTO(null, "New Project", "Description", null, null);
    
    Project savedProject = new Project();
    savedProject.setId(2L);
    savedProject.setTitle("New Project");
    savedProject.setDescription("Description");
    savedProject.setParticipantIds(null); 
    savedProject.setTaskIds(null);
    
    when(projectRepository.save(any(Project.class))).thenReturn(savedProject);

    ProjectDTO result = projectService.createProject(dtoWithNullLists);

    assertNotNull(result);
    assertEquals("New Project", result.getTitle());
    assertNotNull(result.getParticipantIds());
    assertTrue(result.getParticipantIds().isEmpty());
    assertNotNull(result.getTaskIds());
    assertTrue(result.getTaskIds().isEmpty());
    }

    @Test
    void testUpdateProject_AllFields() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        
        Project updatedProject = new Project();
        updatedProject.setId(1L);
        updatedProject.setTitle("Updated Title");
        updatedProject.setDescription("Updated Description");
        updatedProject.setParticipantIds(new ArrayList<>(List.of(1L, 2L, 3L)));
        updatedProject.setTaskIds(new ArrayList<>(List.of(30L, 40L)));
        
        when(projectRepository.save(any(Project.class))).thenReturn(updatedProject);

        ProjectDTO updatedDTO = new ProjectDTO(
            null, 
            "Updated Title", 
            "Updated Description", 
            new ArrayList<>(List.of(1L, 2L, 3L)), 
            new ArrayList<>(List.of(30L, 40L))
        );
        
        ProjectDTO result = projectService.updateProject(1L, updatedDTO);

        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Description", result.getDescription());
        assertEquals(3, result.getParticipantIds().size());
        assertTrue(result.getParticipantIds().contains(3L));
        assertEquals(2, result.getTaskIds().size());
        assertTrue(result.getTaskIds().contains(30L));
    }

    @Test
    void testUpdateProject_WithNullFields() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        
        Project savedProject = new Project();
        savedProject.setId(1L);
        savedProject.setTitle("Test Project"); // Título permanece inalterado
        savedProject.setDescription("This is a test project"); // Descrição permanece inalterada
        savedProject.setParticipantIds(new ArrayList<>(Arrays.asList(1L, 2L)));
        savedProject.setTaskIds(new ArrayList<>(Arrays.asList(10L, 20L)));
        
        when(projectRepository.save(any(Project.class))).thenReturn(savedProject);
    
        // Ambos título e descrição são nulos
        ProjectDTO updatedDTO = new ProjectDTO(null, null, null, null, null);
        ProjectDTO result = projectService.updateProject(1L, updatedDTO);
    
        assertNotNull(result);
        assertEquals("Test Project", result.getTitle()); // Verificando que o título permaneceu inalterado
        assertEquals("This is a test project", result.getDescription()); // Verificando que a descrição permaneceu inalterada
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
        assertEquals(3, result.getParticipantIds().size());
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