package com.example.worksync.controller;

import com.example.worksync.dto.requests.AuthDTO;
import com.example.worksync.dto.responses.LoginResponseDTO;
import com.example.worksync.dto.requests.UserDTO;
import com.example.worksync.model.User;
import com.example.worksync.service.AuthService;
import com.example.worksync.service.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private AuthService authService;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login_shouldReturnOkWithToken() {
        AuthDTO authDTO = new AuthDTO();
        authDTO.setEmail("test@example.com");
        authDTO.setPassword("password");

        User user = new User();
        user.setEmail("test@example.com");

        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(tokenService.generateToken(any(User.class))).thenReturn("testToken");

        ResponseEntity<LoginResponseDTO> response = authController.login(authDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("testToken", response.getBody().getToken());
    }

    @Test
    void register_shouldReturnCreatedWithUser() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("newuser@example.com");
        userDTO.setPassword("newpassword");
        userDTO.setName("New User");

        User registeredUser = new User();
        registeredUser.setEmail("newuser@example.com");
        registeredUser.setName("New User");

        when(authService.register(any(UserDTO.class))).thenReturn(registeredUser);

        ResponseEntity<User> response = authController.register(userDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(registeredUser, response.getBody());
    }

    @Test
    void listUsers_shouldReturnOkWithListOfUsers() {
        User user1 = new User();
        user1.setEmail("user1@example.com");
        User user2 = new User();
        user2.setEmail("user2@example.com");
        List<User> users = Arrays.asList(user1, user2);

        when(authService.listUsers()).thenReturn(users);

        ResponseEntity<List<User>> response = authController.listUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(users, response.getBody());
    }
}
