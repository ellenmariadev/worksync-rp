package com.example.worksync.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.worksync.exceptions.NotFoundException;
import com.example.worksync.model.Notification;
import com.example.worksync.model.User;
import com.example.worksync.repository.NotificationRepostiory;

@Service
public class NotificationService {

    private final NotificationRepostiory notificationRepository;

    public NotificationService(NotificationRepostiory notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public void createNotification(User user, String message, Long taskId) {
        Notification notification = new Notification(user, message, false, taskId);
        notificationRepository.save(notification);
    }

    public List<Notification> getUnreadNotificationsForUser(User user) {
        return notificationRepository.findByUserAndReadFalse(user);
    }

    public Notification markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundException("Notification not found"));

        notification.markAsRead();
        notificationRepository.save(notification);
        return notification;
    }

    public List<Notification> markAllAsRead(User user) {
        List<Notification> unreadNotifications = notificationRepository.findByUserAndReadFalse(user);
        for (Notification notification : unreadNotifications) {
            notification.markAsRead();
        }
        notificationRepository.saveAll(unreadNotifications);
        return unreadNotifications;
    }

}
