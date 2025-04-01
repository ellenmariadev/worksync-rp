package com.example.worksync.integrations;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.example.worksync.service.TaskService;
import com.example.worksync.repository.TaskRepository;

import org.mockito.junit.jupiter.MockitoExtension;

import com.example.worksync.controller.TaskController;
import com.example.worksync.dto.requests.TaskDTO;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.worksync.model.User; // Adjust the package path if necessary

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
class TaskControllerIntegrationTest {

    private MockMvc mockMvc;

    @Mock
    private TaskService taskService;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskController taskController;

    private User testUser;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();

        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        Authentication authentication = org.springframework.security.authentication.UsernamePasswordAuthenticationToken.authenticated(testUser, null, testUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void testCreateTask() throws Exception {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setTitle("Test Task");
        taskDTO.setDescription("Description of test task");
        taskDTO.setProjectId(1L);
        taskDTO.setResponsibleId(1L);

        when(taskService.createTask(any(TaskDTO.class), any(User.class))).thenReturn(taskDTO);

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Test Task\", \"description\":\"Description of test task\", \"projectId\":1, \"responsibleId\":1}")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.description").value("Description of test task"));
    }

    @Test
    void testGetTaskById() throws Exception {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(1L);
        taskDTO.setTitle("Test Task");
        taskDTO.setDescription("Description of test task");

        when(taskService.findById(1L)).thenReturn(Optional.of(taskDTO));

        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.description").value("Description of test task"));
    }
    @Test
    void testUpdateTask() throws Exception {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(1L);
        taskDTO.setTitle("Updated Test Task");
        taskDTO.setDescription("Updated Description of test task");
    
        when(taskService.updateTask(any(Long.class), any(TaskDTO.class))).thenReturn(taskDTO);
    
        mockMvc.perform(patch("/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Updated Test Task\", \"description\":\"Updated Description of test task\"}")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Updated Test Task"))
                .andExpect(jsonPath("$.description").value("Updated Description of test task"));
    }
    
    @Test
    void testDeleteTask() throws Exception {
        when(taskRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/tasks/1"))
                .andExpect(status().isNoContent());

        verify(taskRepository, times(1)).deleteById(1L);
    }
}
