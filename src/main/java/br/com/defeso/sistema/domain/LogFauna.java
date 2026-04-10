package br.com.defeso.sistema.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "log_fauna_deletada")
public class LogFauna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "especie_nome")
    private String especieNome;

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "data_exclusao")
    private LocalDateTime dataExclusao;

    @Column(name = "motivo")
    private String motivo;

    // Construtor padrão necessário para o JPA
    public LogFauna() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEspecieNome() { return especieNome; }
    public void setEspecieNome(String especieNome) { this.especieNome = especieNome; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public LocalDateTime getDataExclusao() { return dataExclusao; }
    public void setDataExclusao(LocalDateTime dataExclusao) { this.dataExclusao = dataExclusao; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}