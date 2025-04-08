package com.example.worksync.model.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserRoleTest {

    @Test
    @DisplayName("Deve retornar a string correta para cada role")
    void testGetRole() {
        assertEquals("ADMIN", UserRole.ADMIN.getRole());
        assertEquals("USER", UserRole.USER.getRole());
    }

    @Test
    @DisplayName("Deve conter os valores esperados no enum")
    void testEnumValues() {
        UserRole[] roles = UserRole.values();
        assertEquals(2, roles.length);
        assertTrue(contains(roles, UserRole.ADMIN));
        assertTrue(contains(roles, UserRole.USER));
    }

    private boolean contains(UserRole[] roles, UserRole role) {
        for (UserRole r : roles) {
            if (r == role) return true;
        }
        return false;
    }
}
