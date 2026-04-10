package br.com.defeso.sistema.dao;

import br.com.defeso.sistema.domain.Contato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ContatoRepository extends JpaRepository<Contato, Long> {

    // 🔹 1. Buscar por usuário (já existia)
    List<Contato> findByUsuarioId(Long usuarioId);


    // 🔥 2. LIKE (busca por mensagem)
    @Query("SELECT c FROM Contato c WHERE LOWER(c.mensagem) LIKE LOWER(CONCAT('%', :texto, '%'))")
    List<Contato> buscarPorMensagem(String texto);


    // 🔥 3. SUBQUERY (usuários que mais enviam mensagens)
    @Query("""
        SELECT c FROM Contato c
        WHERE c.usuario.id IN (
            SELECT c2.usuario.id FROM Contato c2
            GROUP BY c2.usuario.id
            HAVING COUNT(c2) > 3
        )
    """)
    List<Contato> usuariosMaisAtivos();


    // 🔥 4. VIEW (usando query nativa)
    @Query(value = "SELECT * FROM v_feedback_recentes", nativeQuery = true)
    List<Contato> buscarFeedbacksRecentes();
}