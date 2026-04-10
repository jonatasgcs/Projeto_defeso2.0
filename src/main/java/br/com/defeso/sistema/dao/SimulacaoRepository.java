package br.com.defeso.sistema.dao;

import br.com.defeso.sistema.domain.Simulacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SimulacaoRepository extends JpaRepository<Simulacao, Long> {

    // ✔ Já existia
    List<Simulacao> findByUsuarioId(Long usuarioId);


    // =========================
    // 🔥 SUBQUERY (USUÁRIOS QUE MAIS SIMULAM)
    // =========================
    @Query(value =
            "SELECT * FROM simulacoes s WHERE s.usuario_id IN (" +
                    "SELECT usuario_id FROM simulacoes " +
                    "GROUP BY usuario_id HAVING COUNT(*) > 3" +
                    ")",
            nativeQuery = true)
    List<Simulacao> usuariosMaisAtivos();


    // =========================
    // 🔵 VIEW (SIMULAÇÕES RECENTES)
    // =========================
    @Query(value =
            "SELECT * FROM v_simulacoes_recentes",
            nativeQuery = true)
    List<Simulacao> buscarSimulacoesRecentes();


    // =========================
    // 🟡 FILTRO INTELIGENTE (LIKE)
    // =========================
    @Query(value =
            "SELECT * FROM simulacoes s WHERE " +
                    "LOWER(s.especie) LIKE LOWER(CONCAT('%', :filtro, '%')) " +
                    "OR LOWER(s.resultado) LIKE LOWER(CONCAT('%', :filtro, '%'))",
            nativeQuery = true)
    List<Simulacao> buscarComFiltro(String filtro);
}