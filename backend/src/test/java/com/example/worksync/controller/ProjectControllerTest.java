package com.example.worksync.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.Optional;

import com.example.worksync.dto.requests.ProjectDTO;
import com.example.worksync.service.ProjectService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class ProjectControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProjectService projectService;

    private ProjectDTO testProject;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ProjectController projectController = new ProjectController(projectService);
        mockMvc = MockMvcBuilders.standaloneSetup(projectController).build();

        testProject = new ProjectDTO(null, null, null, null, null);
        testProject.setId(1L);
        testProject.setTitle("Test Project");
        testProject.setDescription("Test description");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Deve criar projeto com sucesso")
    void createProject_ShouldReturnCreatedProject() throws Exception {
        when(projectService.createProject(any(ProjectDTO.class))).thenReturn(testProject);

        mockMvc.perform(post("/projects")
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Test Project\",\"description\":\"Test description\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Project"))
                .andExpect(jsonPath("$.description").value("Test description"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Não deve criar projeto sem título")
    void createProjectWithoutTitle_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/projects")
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\":\"Test description\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve listar projetos com filtro")
    void listProjectsWithFilter_ShouldReturnProjects() throws Exception {
        when(projectService.listProjects("Test")).thenReturn(Collections.singletonList(testProject));

        mockMvc.perform(get("/projects").param("title", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Project"));
    }

    @Test
    @WithMockUser
    @DisplayName("Deve buscar projeto por ID existente")
    void getProjectById_ShouldReturnProject() throws Exception {
        when(projectService.findById(1L)).thenReturn(Optional.of(testProject));

        mockMvc.perform(get("/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Project"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Deve atualizar projeto com sucesso")
    void updateProject_ShouldReturnUpdatedProject() throws Exception {
        ProjectDTO updated = new ProjectDTO(null, null, null, null, null);
        updated.setId(1L);
        updated.setTitle("Updated");
        updated.setDescription("Updated description");

        when(projectService.updateProject(eq(1L), any(ProjectDTO.class))).thenReturn(updated);

        mockMvc.perform(patch("/projects/1")
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Updated\",\"description\":\"Updated description\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Deve deletar projeto com sucesso")
    void deleteProject_ShouldReturnNoContent() throws Exception {
        doNothing().when(projectService).deleteProject(1L);

        mockMvc.perform(delete("/projects/1")
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Deve adicionar participante ao projeto")
    void addParticipant_ShouldReturnProject() throws Exception {
        when(projectService.addParticipantToProject(1L, 2L)).thenReturn(testProject);

        mockMvc.perform(post("/projects/1/participants/2")
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Project"));
    }
}
