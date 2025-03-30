package com.example.worksync.event;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.worksync.model.Task;
import com.example.worksync.model.User;
import com.example.worksync.service.NotificationService;

@ExtendWith(MockitoExtension.class)
public class UserTaskAssignmentListenertTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private UserTaskAssignmentListener listener;

    private User user;
    private Task task;
    private UserTaskAssignmentEvent testEvent;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Test User");

        task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");

        testEvent = new UserTaskAssignmentEvent(this, user, task);
    }

    @Test
    void getEventToCreateCreateNotification() {
        listener.handleUserTaskAssignment(testEvent);

        verify(notificationService, times(1)).createNotification(
                eq(user),
                eq("Você foi assinado à tarefa: Test Task"),
                eq(1L)
        );
    }
}