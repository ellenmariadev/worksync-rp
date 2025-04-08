package com.example.worksync.event;

import com.example.worksync.model.Task;
import com.example.worksync.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTaskAssignmentEventTest {

    @Test
    @DisplayName("Deve criar evento com usuário e tarefa corretos")
    void testUserTaskAssignmentEventCreation() {
        User user = new User();
        user.setId(1L);
        user.setName("João");

        Task task = new Task();
        task.setId(10L);
        task.setTitle("Implementar endpoint");

        Object source = new Object();
        UserTaskAssignmentEvent event = new UserTaskAssignmentEvent(source, user, task);

        assertEquals(source, event.getSource());
        assertEquals(user, event.getUser());
        assertEquals(task, event.getTask());
    }

    @Test
    @DisplayName("Deve atualizar usuário e tarefa com os setters")
    void testSetters() {
        User initialUser = new User();
        Task initialTask = new Task();

        UserTaskAssignmentEvent event = new UserTaskAssignmentEvent(new Object(), initialUser, initialTask);

        User newUser = new User();
        newUser.setId(2L);
        newUser.setName("Maria");

        Task newTask = new Task();
        newTask.setId(20L);
        newTask.setTitle("Revisar documentação");

        event.setUser(newUser);
        event.setTask(newTask);

        assertEquals(newUser, event.getUser());
        assertEquals(newTask, event.getTask());
    }
}
