package com.example.worksync.event;

import org.springframework.context.ApplicationEvent;

import com.example.worksync.model.Task;
import com.example.worksync.model.User;

public class UserTaskAssignmentEvent extends ApplicationEvent {
    private User user;
    private Task task;

    public UserTaskAssignmentEvent(Object source, User user, Task task) {
        super(source);
        this.user = user;
        this.task = task;
    }

    public User getUser() {
        return user;
    }

    public Task getTask() {
        return task;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}
