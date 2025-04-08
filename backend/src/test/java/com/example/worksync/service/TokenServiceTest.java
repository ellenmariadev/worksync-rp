package com.example.worksync.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.example.worksync.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    @Mock
    private User mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(tokenService, "secret", "test-secret-key");
        ReflectionTestUtils.setField(tokenService, "expirationTime", 3600);
    }

    @Test
    void shouldThrowExceptionWhenGeneratingTokenFails() {
        when(mockUser.getUsername()).thenThrow(new JWTCreationException("Error", null));

        assertThrows(RuntimeException.class, () -> tokenService.generateToken(mockUser));
    }

    @Test
    void shouldReturnEmptyStringForInvalidToken() {
        String invalidToken = "invalid.token.string";

        String result = tokenService.validateToken(invalidToken);

        assertThat(result).isEmpty();
    }
}
