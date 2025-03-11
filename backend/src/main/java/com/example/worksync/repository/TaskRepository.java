package com.example.worksync.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.worksync.model.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProjectId(Long projectId);
    Optional<Task> findById(Long id);
    boolean existsById(Long id);
    List<Task> findByTitleContainingIgnoreCase(String title);
    List<Task> findByStartDateBetween(LocalDate startDateMin, LocalDate startDateMax);
    List<Task> findTasksWithProjectNameByProjectId(Long projectId);
}
