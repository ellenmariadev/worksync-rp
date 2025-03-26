package com.example.worksync.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.worksync.model.Notification;
import com.example.worksync.model.User;
import com.example.worksync.service.NotificationService;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public List<Notification> getUnreadNotifications(@AuthenticationPrincipal User user) {
        return notificationService.getUnreadNotificationsForUser(user);
    }

    @PostMapping("/{id}/read")
        public ResponseEntity<Notification> markNotificationAsRead(@PathVariable Long id) {
        Notification notification = notificationService.markAsRead(id);
        return ResponseEntity.ok(notification);
    }

    @PostMapping("/read-all")
    public List<Notification> markAllNotificationAsRead(@AuthenticationPrincipal User user) {
        return notificationService.markAllAsRead(user);
    }
}
