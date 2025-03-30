package com.example.worksync.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.worksync.dto.requests.ProjectDTO;
import com.example.worksync.service.ProjectService;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ProjectService projectService;

    private ProjectDTO testProject;

    @BeforeEach
    void setUp() {
        ProjectController projectController = new ProjectController(projectService);
        mockMvc = MockMvcBuilders.standaloneSetup(projectController).build();
        
        testProject = new ProjectDTO(null, null, null, null, null);
        testProject.setId(1L);
        testProject.setTitle("Test Project");
        testProject.setDescription("Test description");
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Create project should return the created project")
    void createProject_ShouldReturnCreatedProject() throws Exception {
        when(projectService.createProject(any(ProjectDTO.class))).thenReturn(testProject);

        mockMvc.perform(post("/projects")
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                .contentType("application/json")
                .content("{\"title\":\"Test Project\",\"description\":\"Test description\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Project"))
                .andExpect(jsonPath("$.description").value("Test description"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Create project without title should return BAD_REQUEST")
    void createProjectWithoutTitle_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/projects")
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                .contentType("application/json")
                .content("{\"description\":\"Test description\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Get project by id should return the project")
    void getProjectById_ShouldReturnProject() throws Exception {
        when(projectService.findById(1L)).thenReturn(java.util.Optional.of(testProject));

        mockMvc.perform(get("/projects/1")
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Project"))
                .andExpect(jsonPath("$.description").value("Test description"));
    }


    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Update project should return the updated project")
    void updateProject_ShouldReturnUpdatedProject() throws Exception {
        ProjectDTO updatedProject = new ProjectDTO(null, null, null, null, null);
        updatedProject.setId(1L);
        updatedProject.setTitle("Updated Project");
        updatedProject.setDescription("Updated description");

        when(projectService.updateProject(any(Long.class), any(ProjectDTO.class))).thenReturn(updatedProject);

        mockMvc.perform(patch("/projects/1")
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                .contentType("application/json")
                .content("{\"title\":\"Updated Project\",\"description\":\"Updated description\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Project"))
                .andExpect(jsonPath("$.description").value("Updated description"));
    }


    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Create project with empty title should return BAD_REQUEST")
    void createProjectWithEmptyTitle_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/projects")
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                .contentType("application/json")
                .content("{\"title\":\"\",\"description\":\"Test description\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Delete project should return no content status")
    void deleteProject_ShouldReturnNoContent() throws Exception {
        doNothing().when(projectService).deleteProject(1L);

        mockMvc.perform(delete("/projects/1")
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Add participant to project should return updated project")
    void addParticipant_ShouldReturnUpdatedProject() throws Exception {
        when(projectService.addParticipantToProject(1L, 2L)).thenReturn(testProject);

        mockMvc.perform(post("/projects/1/participants/2")
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Project"))
                .andExpect(jsonPath("$.description").value("Test description"));
    }


    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("List projects with title filter should return filtered projects")
    void listProjectsWithTitleFilter_ShouldReturnFilteredProjects() throws Exception {
        when(projectService.listProjects("Test Project")).thenReturn(Arrays.asList(testProject));

        mockMvc.perform(get("/projects")
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                .param("title", "Test Project"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Project"));
    }
}
