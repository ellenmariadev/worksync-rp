package com.example.worksync.integrations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.worksync.controller.NotificationController;
import com.example.worksync.event.UserTaskAssignmentEvent;
import com.example.worksync.event.UserTaskAssignmentListener;
import com.example.worksync.model.Notification;
import com.example.worksync.model.Task;
import com.example.worksync.model.User;
import com.example.worksync.service.NotificationService;

@ExtendWith(MockitoExtension.class)
class NotificationIntegrationTest {
    
    @Mock
    private NotificationService notificationService;
    
    @InjectMocks
    private NotificationController notificationController;
    
    @InjectMocks
    private UserTaskAssignmentListener userTaskAssignmentListener;
    
    private MockMvc mockMvc;
        
    private User testUser;
    private Task testTask;
    private Notification testNotification;
    
    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(notificationController).build();
        
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("testuser");
        testUser.setEmail("test@example.com");
        
        testTask = new Task();
        testTask.setId(1L);
        testTask.setTitle("Test Task");
        testTask.setDescription("Test Description");
        
        testNotification = new Notification(testUser, "Você foi assinado à tarefa: Test Task", false, testTask.getId());
        testNotification.setId(1L);
        testNotification.setCreatedAt(LocalDateTime.now());
        
        userTaskAssignmentListener = new UserTaskAssignmentListener(notificationService);
    }
    
    @Test
    void testHandleUserTaskAssignment() {
        UserTaskAssignmentEvent event = new UserTaskAssignmentEvent(this, testUser, testTask);
        
        userTaskAssignmentListener.handleUserTaskAssignment(event);
        
        verify(notificationService).createNotification(testUser, "Você foi assinado à tarefa: Test Task", testTask.getId());
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void testGetUnreadNotifications() throws Exception {
        List<Notification> notifications = Arrays.asList(testNotification);
        
        when(notificationService.getUnreadNotificationsForUser(any(User.class))).thenReturn(notifications);
        
        mockMvc.perform(get("/notifications")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        
        verify(notificationService, times(1)).getUnreadNotificationsForUser(any(User.class));
    }
    
    
    @Test
    void testMarkNotificationAsRead() throws Exception {
        when(notificationService.markAsRead(anyLong())).thenReturn(testNotification);
        
        mockMvc.perform(post("/notifications/1/read")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        
        verify(notificationService).markAsRead(1L);
    }
    
}
