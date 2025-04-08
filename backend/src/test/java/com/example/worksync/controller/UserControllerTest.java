package com.example.worksync.controller;

import com.example.worksync.model.User;
import com.example.worksync.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("GET /users - Deve retornar todos os usuários")
    void testGetAllUsers() throws Exception {
        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("a@a.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("b@b.com");

        List<User> users = Arrays.asList(user1, user2);
        when(userService.findAll()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("GET /users/email/{email} - Usuário encontrado")
    void testGetUserByEmail_Found() throws Exception {
        User user = new User();
        user.setEmail("found@example.com");

        when(userService.findByEmail("found@example.com")).thenReturn(user);

        mockMvc.perform(get("/users/email/found@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("found@example.com"));
    }

    @Test
    @DisplayName("GET /users/email/{email} - Usuário não encontrado")
    void testGetUserByEmail_NotFound() throws Exception {
        when(userService.findByEmail("missing@example.com")).thenReturn(null);

        mockMvc.perform(get("/users/email/missing@example.com"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /users/{id} - Usuário encontrado")
    void testGetUserById_Found() throws Exception {
        User user = new User();
        user.setId(5L);

        when(userService.findById(5L)).thenReturn(user);

        mockMvc.perform(get("/users/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5));
    }

    @Test
    @DisplayName("GET /users/{id} - Usuário não encontrado")
    void testGetUserById_NotFound() throws Exception {
        when(userService.findById(99L)).thenReturn(null);

        mockMvc.perform(get("/users/99"))
                .andExpect(status().isNotFound());
    }
}
