package com.example.worksync.model;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.example.worksync.model.enums.UserRole;


class UserTest {

    @Test
    void testSetPassword() {
        User user = new User();
        user.setPassword("newPassword");
        assertEquals("newPassword", user.getPassword());
    }

    @Test
    void testSetRole() {
        User user = new User();
        user.setRole(UserRole.ADMIN);
        assertEquals(UserRole.ADMIN, user.getRole());
    }

    @Test
    void testSetName() {
        User user = new User();
        user.setName("John Doe");
        assertEquals("John Doe", user.getName());
    }

    @Test
    void testGetAuthoritiesForAdmin() {
        User user = new User();
        user.setRole(UserRole.ADMIN);
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));
        assertEquals(2, authorities.size());
    }

    @Test
    void testGetAuthoritiesForUser() {
        User user = new User();
        user.setRole(UserRole.USER);
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));
        assertEquals(1, authorities.size());
    }

    @Test
    void testToString() {
        User user = new User("test@example.com", "password", UserRole.USER, "Test User");
        user.setId(1L);
        String expectedString = "User [email=test@example.com, id=1, password=password, role=USER, name=Test User]";
        assertEquals(expectedString, user.toString());
    }
}