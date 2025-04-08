package com.example.worksync.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

import com.example.worksync.model.User;
import com.example.worksync.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.Arrays;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    

    @Test
    void shouldFindAllUsers() {
        List<User> users = Arrays.asList(new User("test1@email.com", "pass1", null, "User One"),
                                         new User("test2@email.com", "pass2", null, "User Two"));
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.findAll();

        assertThat(result).isEqualTo(users);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void shouldFindUserById() {
        User user = new User("test@email.com", "pass", null, "Test User");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.findById(1L);

        assertThat(result).isEqualTo(user);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void shouldReturnNullWhenUserNotFoundById() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        User result = userService.findById(1L);

        assertThat(result).isNull();
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void shouldFindUserByEmail() {
        User user = new User("test@email.com", "pass", null, "Test User");
        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(user));

        User result = userService.findByEmail("test@email.com");

        assertThat(result).isEqualTo(user);
        verify(userRepository, times(1)).findByEmail("test@email.com");
    }
    @Test
    void testGetUserById_UserNotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        User result = userService.getUserById(2L);

        assertNull(result);
    }
}

