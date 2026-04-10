package br.com.defeso.sistema.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pesquisas_socio")
public class Pesquisa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipoEmbarcacao;
    private String frequenciaPesca;
    private String usoBeneficio;
    private String satisfacaoProfissao;

    @Column(columnDefinition = "TEXT")
    private String materialUtilizado;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    private LocalDateTime dataEnvio = LocalDateTime.now();

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTipoEmbarcacao() { return tipoEmbarcacao; }
    public void setTipoEmbarcacao(String tipoEmbarcacao) { this.tipoEmbarcacao = tipoEmbarcacao; }
    public String getFrequenciaPesca() { return frequenciaPesca; }
    public void setFrequenciaPesca(String frequenciaPesca) { this.frequenciaPesca = frequenciaPesca; }
    public String getUsoBeneficio() { return usoBeneficio; }
    public void setUsoBeneficio(String usoBeneficio) { this.usoBeneficio = usoBeneficio; }
    public String getSatisfacaoProfissao() { return satisfacaoProfissao; }
    public void setSatisfacaoProfissao(String satisfacaoProfissao) { this.satisfacaoProfissao = satisfacaoProfissao; }
    public String getMaterialUtilizado() { return materialUtilizado; }
    public void setMaterialUtilizado(String materialUtilizado) { this.materialUtilizado = materialUtilizado; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}