package com.example.worksync.service;

import com.example.worksync.dto.TarefaDTO;
import com.example.worksync.model.Tarefa;
import com.example.worksync.repository.TarefaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TarefaService {

    @Autowired
    private TarefaRepository tarefaRepository;

    public List<TarefaDTO> listarTarefasPorProjeto(Long projetoId) {
        List<Tarefa> tarefas = tarefaRepository.findByProjetoId(projetoId);
        return tarefas.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<TarefaDTO> buscarPorId(Long id) {
        return tarefaRepository.findById(id).map(this::convertToDTO);
    }

    public TarefaDTO criarTarefa(TarefaDTO dto) {
        Tarefa tarefa = convertToEntity(dto);
        tarefa = tarefaRepository.save(tarefa);
        return convertToDTO(tarefa);
    }

    public TarefaDTO atualizarTarefa(Long id, TarefaDTO dto) {
        if (!tarefaRepository.existsById(id)) {
            throw new RuntimeException("Tarefa não encontrada!");
        }
        Tarefa tarefa = convertToEntity(dto);
        tarefa.setId(id);
        tarefa = tarefaRepository.save(tarefa);
        return convertToDTO(tarefa);
    }

    private TarefaDTO convertToDTO(Tarefa tarefa) {
        return new TarefaDTO(
                tarefa.getId(),
                tarefa.getTitulo(),
                tarefa.getDescricao(),
                tarefa.getStatus(),
                tarefa.getDataInicio(),
                tarefa.getDataConclusao(),
                tarefa.getPrazoEntrega(),
                tarefa.getPessoalResponsavel().getId(),
                tarefa.getProjeto().getId()
        );
    }

    private Tarefa convertToEntity(TarefaDTO dto) {
        Tarefa tarefa = new Tarefa();
        tarefa.setTitulo(dto.getTitulo());
        tarefa.setDescricao(dto.getDescricao());
        tarefa.setStatus(dto.getStatus());
        tarefa.setDataInicio(dto.getDataInicio());
        tarefa.setDataConclusao(dto.getDataConclusao());
        tarefa.setPrazoEntrega(dto.getPrazoEntrega());
        return tarefa;
    }
}
