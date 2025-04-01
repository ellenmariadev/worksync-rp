package com.example.worksync.controller;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.worksync.model.User;
import com.example.worksync.service.UserService;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllUsers_shouldReturnOkWithListOfUsers() {
        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("user1@example.com");
        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("user2@example.com");
        List<User> users = Arrays.asList(user1, user2);

        when(userService.findAll()).thenReturn(users);

        ResponseEntity<List<User>> response = userController.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(users, response.getBody());
    }

    @Test
    void getUserByEmail_shouldReturnOkWithUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        when(userService.findByEmail("test@example.com")).thenReturn(user);

        ResponseEntity<User> response = userController.getUserByEmail("test@example.com");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    void getUserByEmail_shouldReturnNotFoundWhenUserNotFound() {
        when(userService.findByEmail("nonexistent@example.com")).thenReturn(null);

        ResponseEntity<User> response = userController.getUserByEmail("nonexistent@example.com");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getUserById_shouldReturnOkWithUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        when(userService.findById(1L)).thenReturn(user);

        ResponseEntity<User> response = userController.getUserById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    void getUserById_shouldReturnNotFoundWhenUserNotFound() {
        when(userService.findById(999L)).thenReturn(null);

        ResponseEntity<User> response = userController.getUserById(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }
}
