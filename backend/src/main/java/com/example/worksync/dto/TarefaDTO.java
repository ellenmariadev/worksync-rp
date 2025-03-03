package com.example.worksync.dto;

import java.time.LocalDate;
import java.util.List;

import com.example.worksync.model.enums.StatusTarefa;

public class TarefaDTO {
    private Long id;
    private String titulo;
    private String descricao;
    private StatusTarefa status;
    private LocalDate dataInicio;
    private LocalDate dataConclusao;
    private LocalDate prazoEntrega;
    private Long responsavelId;
    private Long projetoId;
    private List<String> comentarios;

    public TarefaDTO(Long id, String titulo, String descricao, StatusTarefa status, LocalDate dataInicio, LocalDate dataConclusao, LocalDate prazoEntrega, Long responsavelId, Long projetoId, List<String> comentarios) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.status = status;
        this.dataInicio = dataInicio;
        this.dataConclusao = dataConclusao;
        this.prazoEntrega = prazoEntrega;
        this.responsavelId = responsavelId;
        this.projetoId = projetoId;
        this.comentarios = comentarios;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public StatusTarefa getStatus() { return status; }
    public void setStatus(StatusTarefa status) { this.status = status; }

    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }

    public LocalDate getDataConclusao() { return dataConclusao; }
    public void setDataConclusao(LocalDate dataConclusao) { this.dataConclusao = dataConclusao; }

    public LocalDate getPrazoEntrega() { return prazoEntrega; }
    public void setPrazoEntrega(LocalDate prazoEntrega) { this.prazoEntrega = prazoEntrega; }

    public Long getResponsavelId() { return responsavelId; }
    public void setResponsavelId(Long responsavelId) { this.responsavelId = responsavelId; }

    public Long getProjetoId() { return projetoId; }
    public void setProjetoId(Long projetoId) { this.projetoId = projetoId; }

    public List<String> getComentarios() { return comentarios; }
    public void setComentarios(List<String> comentarios) { this.comentarios = comentarios; }
}
