package com.example.worksync.controller;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.worksync.model.Notification;
import com.example.worksync.model.User;
import com.example.worksync.service.NotificationService;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private NotificationService notificationService;

    private User testUser;
    private Notification testNotification;
    private List<Notification> testNotifications;

    @BeforeEach
    void setUp() {
        NotificationController notificationController = new NotificationController(notificationService);
        mockMvc = MockMvcBuilders.standaloneSetup(notificationController).build();
        
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("testuser");
        
        testNotification = new Notification(testUser, "Test message", false, 1L);
        testNotification.setId(1L);
        Notification notification2 = new Notification(testUser, "Another message", false, 2L);
        notification2.setId(2L);
        
        testNotifications = Arrays.asList(testNotification, notification2);
    }

    @Test
    @WithMockUser(username = "testuser")
    @DisplayName("Get unread notifications should return a list of unread notifications")
    void getUnreadNotifications_ShouldReturnUnreadNotifications() throws Exception {
        when(notificationService.getUnreadNotificationsForUser(any(User.class))).thenReturn(testNotifications);
        
        mockMvc.perform(get("/notifications")
                .with(SecurityMockMvcRequestPostProcessors.user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].message").value("Test message"))
                .andExpect(jsonPath("$[1].message").value("Another message"));
    }
    
    @Test
    @WithMockUser(username = "testuser")
    @DisplayName("Mark notification as read should return updated notification")
    void markNotificationAsRead_ShouldReturnUpdatedNotification() throws Exception {
        when(notificationService.markAsRead(1L)).thenReturn(testNotification);
        testNotification.markAsRead();
        
        mockMvc.perform(post("/notifications/1/read")
                .with(SecurityMockMvcRequestPostProcessors.user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.message").value("Test message"))
                .andExpect(jsonPath("$.read").value(true));
    }
    
    @Test
    @WithMockUser(username = "testuser")
    @DisplayName("Mark all notifications as read should return updated notifications")
    void markAllNotificationsAsRead_ShouldReturnUpdatedNotifications() throws Exception {
        for (Notification notification : testNotifications) {
            notification.markAsRead();
        }
        when(notificationService.markAllAsRead(any(User.class))).thenReturn(testNotifications);
        
        mockMvc.perform(post("/notifications/read-all")
                .with(SecurityMockMvcRequestPostProcessors.user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].read").value(true))
                .andExpect(jsonPath("$[1].read").value(true));
    }
}