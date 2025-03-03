package com.example.worksync.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.worksync.dto.TarefaDTO;
import com.example.worksync.service.TarefaService;

@RestController
@RequestMapping("/tarefas")
public class TarefaController {

    @Autowired
    private TarefaService tarefaService;

    @GetMapping("/projeto/{projetoId}")
    public ResponseEntity<List<TarefaDTO>> listarTarefasPorProjeto(@PathVariable Long projetoId) {
        List<TarefaDTO> tarefas = tarefaService.listarTarefasPorProjeto(projetoId);
        return ResponseEntity.ok(tarefas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TarefaDTO> buscarTarefaPorId(@PathVariable Long id) {
        Optional<TarefaDTO> tarefa = tarefaService.buscarPorId(id);
        return tarefa.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TarefaDTO> criarTarefa(@RequestBody TarefaDTO tarefaDTO) {
        TarefaDTO novaTarefa = tarefaService.criarTarefa(tarefaDTO);
        return ResponseEntity.ok(novaTarefa);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TarefaDTO> atualizarTarefa(@PathVariable Long id, @RequestBody TarefaDTO tarefaDTO) {
        TarefaDTO tarefaAtualizada = tarefaService.atualizarTarefa(id, tarefaDTO);
        return ResponseEntity.ok(tarefaAtualizada);
    }
}
