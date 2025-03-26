package com.example.worksync.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.example.worksync.model.Task;
import com.example.worksync.model.User;
import com.example.worksync.service.NotificationService;

@Component
public class UserTaskAssignmentListener {
    private final NotificationService notificationService;

    public UserTaskAssignmentListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @EventListener
    public void handleUserTaskAssignment(UserTaskAssignmentEvent event) {
        User user = event.getUser();
        Task task = event.getTask();
        String message = "Você foi assinado à tarefa: " + task.getTitle();
        notificationService.createNotification(user, message, task.getId());
    }
}
