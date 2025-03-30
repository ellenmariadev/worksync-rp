package com.example.worksync.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.worksync.exceptions.NotFoundException;
import com.example.worksync.model.Notification;
import com.example.worksync.model.User;
import com.example.worksync.repository.NotificationRepository;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    private User testUser;
    private Notification notification;
    private List<Notification> notificationsList;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");

        notification = new Notification(testUser, "Test message", false, 1L);
        notification.setId(1L);

        Notification notification2 = new Notification(testUser, "Another message", false, 2L);
        notification2.setId(2L);

        notificationsList = Arrays.asList(notification, notification2);
    }

    @Test
    @DisplayName("Should create notification successfully")
    void testCreateNotification() {
        String message = "Test message";
        Long taskId = 1L;

        notificationService.createNotification(testUser, message, taskId);

        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    @DisplayName("Should get unread notifications for user")
    void testGetUnreadNotifications() {
        when(notificationRepository.findByUserAndReadFalse(testUser)).thenReturn(notificationsList);

        List<Notification> result = notificationService.getUnreadNotificationsForUser(testUser);

        assertEquals(2, result.size());
        verify(notificationRepository, times(1)).findByUserAndReadFalse(testUser);
    }

    @Test
    @DisplayName("Should mark notification as read")
    void testMarkNotificationAsRead() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        Notification result = notificationService.markAsRead(1L);

        assertTrue(result.isRead());
        verify(notificationRepository, times(1)).save(notification);
    }

    @Test
    @DisplayName("Should throw NotFoundException when marking non-existent notification as read")
    void testNotFoundNotificationToMarkAsRead() {
        when(notificationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            notificationService.markAsRead(99L);
        });
    }

    @Test
    @DisplayName("Should throw NotFoundException when marking non-existent notification as read")
    void testMarkAllNotificationsAsRead() {
        when(notificationRepository.findByUserAndReadFalse(testUser)).thenReturn(notificationsList);

        List<Notification> result = notificationService.markAllAsRead(testUser);

        assertEquals(2, result.size());
        assertTrue(result.get(0).isRead());
        assertTrue(result.get(1).isRead());
        verify(notificationRepository, times(1)).saveAll(notificationsList);
    }
}