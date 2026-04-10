package br.com.defeso.sistema.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import br.com.defeso.sistema.domain.RegistroFauna;
import java.util.List;

public interface RegistroFaunaRepository extends JpaRepository<RegistroFauna, Long> {

    List<RegistroFauna> findByUsuarioId(Long usuarioId);

    // Corrigido: agora busca apenas a coluna correta da VIEW
    @Query(value = "SELECT especie FROM v_ultimas_especies", nativeQuery = true)
    List<String> buscarEspeciesNoRadar();

    // Query simplificada usando a coluna "integridade"
    @Query(value = "SELECT r.id, r.especie, r.localizacao, r.foto_nome, r.data_avistamento, " +
            "r.usuario_id, r.observacoes, r.integridade " +
            "FROM registro_fauna r " +
            "WHERE r.especie LIKE %:busca%",
            nativeQuery = true)
    List<RegistroFauna> buscarComInteligencia(@Param("busca") String busca);
}