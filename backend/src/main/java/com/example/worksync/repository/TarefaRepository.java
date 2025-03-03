package com.example.worksync.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.worksync.model.Tarefa;

public interface TarefaRepository extends JpaRepository<Tarefa, Long> {
    List<Tarefa> findByProjetoId(Long projetoId);
    Optional<Tarefa> findById(Long id);
    boolean existsById(Long id);
}
