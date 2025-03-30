// package com.example.worksync.integrations;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertTrue;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.anyLong;
// import static org.mockito.ArgumentMatchers.eq;
// import static org.mockito.Mockito.times;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.when;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// import java.time.LocalDateTime;
// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.List;
// import java.util.Optional;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.http.MediaType;
// import org.springframework.security.test.context.support.WithMockUser;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.setup.MockMvcBuilders;

// import com.example.worksync.controller.NotificationController;
// import com.example.worksync.event.UserTaskAssignmentEvent;
// import com.example.worksync.event.UserTaskAssignmentListener;
// import com.example.worksync.model.Notification;
// import com.example.worksync.model.Task;
// import com.example.worksync.model.User;
// import com.example.worksync.repository.NotificationRepository;
// import com.example.worksync.service.NotificationService;

// @ExtendWith(MockitoExtension.class)
// public class NotificationIntegrationTest {

//     @Mock
//     private NotificationRepository notificationRepository;
    
//     @Mock
//     private NotificationService notificationService;
    
//     @InjectMocks
//     private NotificationController notificationController;
    
//     @InjectMocks
//     private UserTaskAssignmentListener userTaskAssignmentListener;
    
//     private MockMvc mockMvc;
        
//     private User testUser;
//     private Task testTask;
//     private Notification testNotification;
    
//     @BeforeEach
//     public void setup() {
//         mockMvc = MockMvcBuilders.standaloneSetup(notificationController).build();
        
//         testUser = new User();
//         testUser.setId(1L);
//         testUser.setName("testuser");
//         testUser.setEmail("test@example.com");
        
//         testTask = new Task();
//         testTask.setId(1L);
//         testTask.setTitle("Test Task");
//         testTask.setDescription("Test Description");
        
//         testNotification = new Notification(testUser, "Você foi assinado à tarefa: Test Task", false, testTask.getId());
//         testNotification.setId(1L);
//         testNotification.setCreatedAt(LocalDateTime.now());
        
//         userTaskAssignmentListener = new UserTaskAssignmentListener(notificationService);
//     }
    
//     @Test
//     public void testHandleUserTaskAssignment() {
//         UserTaskAssignmentEvent event = new UserTaskAssignmentEvent(this, testUser, testTask);
        
//         userTaskAssignmentListener.handleUserTaskAssignment(event);
        
//         verify(notificationService).createNotification(
//                 eq(testUser), 
//                 eq("Você foi assinado à tarefa: Test Task"), 
//                 eq(testTask.getId())
//         );
//     }
    
//     @Test
//     @WithMockUser(username = "testuser")
//     public void testGetUnreadNotifications() throws Exception {
//         List<Notification> notifications = Arrays.asList(testNotification);
        
//         when(notificationService.getUnreadNotificationsForUser(any(User.class))).thenReturn(notifications);
        
//         verify(notificationService, times(1)).getUnreadNotificationsForUser(any(User.class));
//     }
    
//     @Test
//     public void testMarkNotificationAsRead() throws Exception {
//         when(notificationService.markAsRead(anyLong())).thenReturn(testNotification);
        
//         mockMvc.perform(post("/notifications/1/read")
//                 .contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isOk());
        
//         verify(notificationService).markAsRead(1L);
//     }
    
//     @Test
//     @WithMockUser(username = "testuser")
//     public void testMarkAllNotificationsAsRead() throws Exception {
//         List<Notification> readNotifications = new ArrayList<>();
//         testNotification.setRead(true);
//         readNotifications.add(testNotification);
        
//         when(notificationService.markAllAsRead(any(User.class))).thenReturn(readNotifications);
        
//         mockMvc.perform(post("/notifications/read-all")
//                 .contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isOk());
        
//         verify(notificationService).markAllAsRead(any(User.class));
//     }
    
//     @Test
//     public void testNotificationServiceCreateNotification() {
//         NotificationService service = new NotificationService(notificationRepository);
        
//         when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> {
//             Notification savedNotification = invocation.getArgument(0);
//             savedNotification.setId(1L);
//             return savedNotification;
//         });
        
//         service.createNotification(testUser, "Test message", 1L);
//         verify(notificationRepository).save(any(Notification.class));
//     }
    
//     @Test
//     public void testNotificationServiceMarkAsRead() {
//         NotificationService service = new NotificationService(notificationRepository);
        
//         Notification notification = new Notification(testUser, "Test message", false, 1L);
//         notification.setId(1L);
        
//         when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
//         when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
        
//         Notification result = service.markAsRead(1L);
        
//         assertTrue(result.isRead());
//         verify(notificationRepository).save(notification);
//     }
    
//     @Test
//     public void testNotificationServiceMarkAllAsRead() {
//         NotificationService service = new NotificationService(notificationRepository);
        
//         Notification notification1 = new Notification(testUser, "Message 1", false, 1L);
//         Notification notification2 = new Notification(testUser, "Message 2", false, 2L);
//         List<Notification> unreadNotifications = Arrays.asList(notification1, notification2);
        
//         when(notificationRepository.findByUserAndReadFalse(testUser)).thenReturn(unreadNotifications);
//         when(notificationRepository.saveAll(any())).thenReturn(unreadNotifications);
        
//         List<Notification> result = service.markAllAsRead(testUser);
        
//         assertEquals(2, result.size());
//         assertTrue(result.get(0).isRead());
//         assertTrue(result.get(1).isRead());
//         verify(notificationRepository).saveAll(unreadNotifications);
//     }
// }