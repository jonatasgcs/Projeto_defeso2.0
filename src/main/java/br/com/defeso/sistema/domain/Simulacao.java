package br.com.defeso.sistema.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "simulacoes")
public class Simulacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer tempoRegistro;
    private String especie;
    private String outraRenda;
    private String outroBeneficio;

    private String resultado; // APROVADO / REPROVADO

    // 🔥 NOVO (CASE do banco)
    private String classificacao; // ALTA CHANCE / BAIXA CHANCE

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    private LocalDateTime dataSimulacao = LocalDateTime.now();

    // =====================
    // GETTERS E SETTERS
    // =====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getTempoRegistro() {
        return tempoRegistro;
    }

    public void setTempoRegistro(Integer tempoRegistro) {
        this.tempoRegistro = tempoRegistro;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public String getOutraRenda() {
        return outraRenda;
    }

    public void setOutraRenda(String outraRenda) {
        this.outraRenda = outraRenda;
    }

    public String getOutroBeneficio() {
        return outroBeneficio;
    }

    public void setOutroBeneficio(String outroBeneficio) {
        this.outroBeneficio = outroBeneficio;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public String getClassificacao() {
        return classificacao;
    }

    public void setClassificacao(String classificacao) {
        this.classificacao = classificacao;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getDataSimulacao() {
        return dataSimulacao;
    }

    public void setDataSimulacao(LocalDateTime dataSimulacao) {
        this.dataSimulacao = dataSimulacao;
    }
}