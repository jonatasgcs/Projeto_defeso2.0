package br.com.defeso.sistema.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "registro_fauna")
public class RegistroFauna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String especie;
    private String localizacao;
    private String observacoes;

    @Column(name = "foto_nome")
    private String fotoNome;

    @Column(name = "data_avistamento")
    private LocalDate dataAvistamento;

    @Column(name = "usuario_id")
    private Long usuarioId;

    @ManyToOne
    @JoinColumn(name = "usuario_id", insertable = false, updatable = false)
    private Usuario usuario;

    // NOVA COLUNA DE INTEGRIDADE (vinda da PROCEDURE)
    @Column(name = "integridade")
    private String integridade;

    // GETTERS E SETTERS
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEspecie() { return especie; }
    public void setEspecie(String especie) { this.especie = especie; }

    public String getLocalizacao() { return localizacao; }
    public void setLocalizacao(String localizacao) { this.localizacao = localizacao; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public String getFotoNome() { return fotoNome; }
    public void setFotoNome(String fotoNome) { this.fotoNome = fotoNome; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public LocalDate getDataAvistamento() { return dataAvistamento; }
    public void setDataAvistamento(LocalDate dataAvistamento) { this.dataAvistamento = dataAvistamento; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getIntegridade() { return integridade; }
    public void setIntegridade(String integridade) { this.integridade = integridade; }
}