package com.example.worksync.controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.worksync.dto.requests.ProjectDTO;
import com.example.worksync.exceptions.NotFoundException;
import com.example.worksync.model.User;
import com.example.worksync.service.ProjectService;

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
    @DisplayName("Não deve criar projeto sem título ou com título vazio")
    void createProjectWithoutTitle_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/projects")
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"\",\"description\":\"Test description\"}"))
                .andExpect(status().isBadRequest());
    
        mockMvc.perform(post("/projects")
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\":\"Test description\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve lançar NotFoundException quando projeto não encontrado")
    void getProjectById_WithNonExistingId_ShouldThrowNotFoundException() throws Exception {
        Long nonExistingId = 999L;
        when(projectService.findById(nonExistingId)).thenReturn(Optional.empty());

        try {
            mockMvc.perform(get("/projects/{id}", nonExistingId));
        } catch (Exception ex) {
            Throwable rootCause = ex.getCause();
            assertTrue(rootCause instanceof NotFoundException);
            assertEquals("Project not found!", rootCause.getMessage());

            verify(projectService).findById(nonExistingId);
            return;
        }

        fail("Expected NotFoundException was not thrown");
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

    @Test
    @WithMockUser
    @DisplayName("Deve listar participantes de um projeto corretamente")
    void getParticipants_ShouldReturnParticipantsList() throws Exception {
        Long projectId = 1L;
        Long authenticatedUserId = 5L;

        User user1 = new User();
        user1.setId(1L);
        user1.setName("user1");
        user1.setEmail("user1@example.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setName("user2");
        user2.setEmail("user2@example.com");

        List<User> participants = Arrays.asList(user1, user2);

        when(projectService.getParticipants(eq(projectId), any())).thenReturn(participants);

        mockMvc.perform(get("/projects/{id}/participants", projectId)
                .with(SecurityMockMvcRequestPostProcessors.user(authenticatedUserId.toString())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].username").value("user1@example.com"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].username").value("user2@example.com"));

        verify(projectService).getParticipants(eq(projectId), any());
    }
}
