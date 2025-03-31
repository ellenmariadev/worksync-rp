package com.example.worksync.service;

import com.example.worksync.dto.requests.ProjectDTO;
import com.example.worksync.exceptions.NotFoundException;
import com.example.worksync.model.Project;
import com.example.worksync.repository.ProjectRepository;
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

public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectService projectService;

    private Project project;
    private ProjectDTO projectDTO;

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

        assertThrows(NotFoundException.class, () -> projectService.updateProject(1L, projectDTO));
    }

    @Test
    void testDeleteProject_NotFound() {
        when(projectRepository.existsById(1L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> projectService.deleteProject(1L));
    }

    @Test
    void testAddParticipantToProject_Success() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        ProjectDTO result = projectService.addParticipantToProject(1L, 3L);

        assertNotNull(result);
        assertTrue(result.getParticipantIds().contains(3L));
    }

    @Test
    void testAddParticipantToProject_AlreadyExists() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        assertThrows(RuntimeException.class, () -> projectService.addParticipantToProject(1L, 1L));
    }

    @Test
    void testAddParticipantToProject_NotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> projectService.addParticipantToProject(1L, 3L));

    }
}
