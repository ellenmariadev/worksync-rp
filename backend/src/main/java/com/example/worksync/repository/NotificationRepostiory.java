package com.example.worksync.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.worksync.model.Notification;
import com.example.worksync.model.User;

@Repository
public interface NotificationRepostiory extends JpaRepository<Notification, Long> {
    List<Notification> findByUserAndReadFalse(User user);

    long countByUserAndReadFalse(User user);
}
