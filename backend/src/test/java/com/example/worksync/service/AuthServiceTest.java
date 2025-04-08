package com.example.worksync.service;

import com.example.worksync.dto.requests.UserDTO;
import com.example.worksync.exceptions.ConflictException;
import com.example.worksync.model.User;
import com.example.worksync.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private UserRepository userRepository;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        authService = new AuthService(userRepository);
    }

    @Test
    void shouldLoadUserByUsername_WhenUserExists() {
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        var result = authService.loadUserByUsername(email);

        assertEquals(user, result);
        verify(userRepository).findByEmail(email);
    }

    @Test
    void shouldThrowException_WhenUserDoesNotExist() {
        String email = "notfound@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> authService.loadUserByUsername(email));
        verify(userRepository).findByEmail(email);
    }

    @Test
    void shouldRegisterUser_WhenEmailIsUnique() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("newuser@example.com");
        userDTO.setPassword("password123");
        userDTO.setName("New User");

        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        User savedUser = authService.register(userDTO);

        verify(userRepository).findByEmail(userDTO.getEmail());
        verify(userRepository).save(userCaptor.capture());

        User captured = userCaptor.getValue();
        assertNotEquals(userDTO.getPassword(), captured.getPassword()); 
        assertTrue(new BCryptPasswordEncoder().matches(userDTO.getPassword(), captured.getPassword()));
        assertEquals(userDTO.getName(), captured.getName());
        assertEquals(userDTO.getRole(), captured.getRole());

        assertEquals(savedUser, captured);
    }

    @Test
    void shouldThrowConflictException_WhenEmailIsAlreadyTaken() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("existing@example.com");

        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.of(new User()));

        assertThrows(ConflictException.class, () -> authService.register(userDTO));
        verify(userRepository).findByEmail(userDTO.getEmail());
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldReturnListOfUsers() {
        User user1 = new User();
        user1.setEmail("user1@example.com");

        User user2 = new User();
        user2.setEmail("user2@example.com");

        List<User> users = List.of(user1, user2);
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = authService.listUsers();

        assertEquals(2, result.size());
        assertEquals(users, result);
        verify(userRepository).findAll();
    }
}
