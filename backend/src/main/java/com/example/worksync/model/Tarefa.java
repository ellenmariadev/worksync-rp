package com.example.worksync.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

import com.example.worksync.model.enums.StatusTarefa;

@Entity
@Table(name = "tarefas")
public class Tarefa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String descricao;

    @Enumerated(EnumType.STRING)
    private StatusTarefa status;

    private LocalDate dataInicio;
    private LocalDate dataConclusao;
    private LocalDate prazoEntrega;

    @ManyToOne
    @JoinColumn(name = "responsavel_id", nullable = false)
    private User responsavel;

    @ManyToOne
    @JoinColumn(name = "projeto_id", nullable = false)
    private Projeto projeto;

    @OneToMany(mappedBy = "tarefa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comentario> comentarios;

    // Construtores
    public Tarefa() {}

    public Tarefa(String titulo, String descricao, StatusTarefa status, LocalDate dataInicio, LocalDate dataConclusao, LocalDate prazoEntrega, User responsavel, Projeto projeto) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.status = status;
        this.dataInicio = dataInicio;
        this.dataConclusao = dataConclusao;
        this.prazoEntrega = prazoEntrega;
        this.responsavel = responsavel;
        this.projeto = projeto;
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

    public User getResponsavel() { return responsavel; }
    public void setResponsavel(User responsavel) { this.responsavel = responsavel; }

    public Projeto getProjeto() { return projeto; }
    public void setProjeto(Projeto projeto) { this.projeto = projeto; }

    public List<Comentario> getComentarios() { return comentarios; }
    public void setComentarios(List<Comentario> comentarios) { this.comentarios = comentarios; }
}

