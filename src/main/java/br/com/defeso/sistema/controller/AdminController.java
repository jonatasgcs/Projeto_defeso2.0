package br.com.defeso.sistema.controller;

import br.com.defeso.sistema.dao.*;
import br.com.defeso.sistema.domain.*;
import jakarta.servlet.http.HttpSession;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.transaction.annotation.Transactional;

@Controller
public class AdminController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RegistroFaunaRepository registroFaunaRepository;

    @Autowired
    private LogFaunaRepository logFaunaRepository;

    @Autowired
    private SimulacaoRepository simulacaoRepository;

    @Autowired
    private ContatoRepository contatoRepository;

    @Autowired
    private PesquisaRepository pesquisaRepository;

    @Autowired
    private LogFeedbackRepository logFeedbackRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @EventListener(ApplicationReadyEvent.class)
    public void inicializarBanco() {
        transactionTemplate.execute(status -> {
            try {

                // =========================
                // 🔵 FAUNA
                // =========================

                entityManager.createNativeQuery(
                        "CREATE TABLE IF NOT EXISTS log_fauna_deletada (" +
                                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                                "especie_nome VARCHAR(255), " +
                                "usuario_id BIGINT, " +
                                "data_exclusao DATETIME, " +
                                "motivo VARCHAR(255))"
                ).executeUpdate();

                entityManager.createNativeQuery(
                        "CREATE OR REPLACE VIEW v_ultimas_especies AS " +
                                "SELECT especie FROM registro_fauna " +
                                "GROUP BY especie " +
                                "ORDER BY MAX(id) DESC LIMIT 6"
                ).executeUpdate();

                entityManager.createNativeQuery("DROP TRIGGER IF EXISTS tg_backup_fauna").executeUpdate();

                entityManager.createNativeQuery(
                        "CREATE TRIGGER tg_backup_fauna " +
                                "BEFORE DELETE ON registro_fauna " +
                                "FOR EACH ROW " +
                                "INSERT INTO log_fauna_deletada (especie_nome, usuario_id, data_exclusao, motivo) " +
                                "VALUES (OLD.especie, OLD.usuario_id, NOW(), 'Exclusão via Admin')"
                ).executeUpdate();


                // =========================
                // 🟡 FEEDBACK
                // =========================

                entityManager.createNativeQuery(
                        "CREATE TABLE IF NOT EXISTS log_feedback_deletado (" +
                                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                                "mensagem TEXT, " +
                                "usuario_id BIGINT, " +
                                "data_exclusao DATETIME)"
                ).executeUpdate();

                entityManager.createNativeQuery("DROP TRIGGER IF EXISTS tg_backup_feedback").executeUpdate();

                entityManager.createNativeQuery(
                        "CREATE TRIGGER tg_backup_feedback " +
                                "BEFORE DELETE ON mensagens_contato " +
                                "FOR EACH ROW " +
                                "INSERT INTO log_feedback_deletado (mensagem, usuario_id, data_exclusao) " +
                                "VALUES (OLD.mensagem, OLD.usuario_id, NOW())"
                ).executeUpdate();


                // =========================
                // 🟢 SIMULADOR (NOVO)
                // =========================

                entityManager.createNativeQuery(
                        "CREATE TABLE IF NOT EXISTS log_simulacao_deletada (" +
                                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                                "especie VARCHAR(255), " +
                                "resultado VARCHAR(50), " +
                                "usuario_id BIGINT, " +
                                "data_exclusao DATETIME)"
                ).executeUpdate();

                entityManager.createNativeQuery("DROP TRIGGER IF EXISTS tg_backup_simulacao").executeUpdate();

                entityManager.createNativeQuery(
                        "CREATE TRIGGER tg_backup_simulacao " +
                                "BEFORE DELETE ON simulacoes " +
                                "FOR EACH ROW " +
                                "INSERT INTO log_simulacao_deletada (especie, resultado, usuario_id, data_exclusao) " +
                                "VALUES (OLD.especie, OLD.resultado, OLD.usuario_id, NOW())"
                ).executeUpdate();

                try {
                    entityManager.createNativeQuery(
                            "SELECT classificacao FROM simulacoes LIMIT 1"
                    ).getResultList();
                } catch (Exception e) {
                    entityManager.createNativeQuery(
                            "ALTER TABLE simulacoes ADD COLUMN classificacao VARCHAR(20)"
                    ).executeUpdate();
                }

                entityManager.createNativeQuery("DROP PROCEDURE IF EXISTS sp_classificar_simulacao").executeUpdate();

                entityManager.createNativeQuery(
                        "CREATE PROCEDURE sp_classificar_simulacao() " +
                                "UPDATE simulacoes SET classificacao = " +
                                "CASE " +
                                "WHEN resultado = 'APROVADO' THEN 'ALTO' " +
                                "WHEN resultado = 'REPROVADO' THEN 'BAIXO' " +
                                "ELSE 'MEDIO' END"
                ).executeUpdate();

                entityManager.createNativeQuery(
                        "CREATE OR REPLACE VIEW v_simulacoes_recentes AS " +
                                "SELECT * FROM simulacoes ORDER BY data_simulacao DESC LIMIT 10"
                ).executeUpdate();

                System.out.println(">>> BANCO COMPLETO <<<");

            } catch (Exception e) {
                System.err.println("Erro ao configurar Banco: " + e.getMessage());
            }
            return null;
        });
    }

    @GetMapping("/admin/painel")
    public String painelAdmin(HttpSession session, Model model) {

        if (session.getAttribute("usuario_id") == null ||
                !"admin".equals(session.getAttribute("usuario_nivel"))) {
            return "redirect:/";
        }

        // DASHBOARD
        model.addAttribute("totalUsuarios", usuarioRepository.count());
        model.addAttribute("totalFauna", registroFaunaRepository.count());
        model.addAttribute("totalContatos", contatoRepository.count());
        model.addAttribute("totalSimulacoes", simulacaoRepository.count());

        entityManager.clear();

        // FAUNA
        model.addAttribute("listaFauna", registroFaunaRepository.buscarComInteligencia(""));
        model.addAttribute("resumoView", registroFaunaRepository.buscarEspeciesNoRadar());
        model.addAttribute("listaLogsExclusao", logFaunaRepository.findAllByOrderByDataExclusaoDesc());

        // FEEDBACK
        model.addAttribute("listaContatos", contatoRepository.findAll());
        model.addAttribute("listaLogsFeedback",
                logFeedbackRepository.findAllByOrderByDataExclusaoDesc()
        );

        // SIMULADOR
        model.addAttribute("listaSimulacoes", simulacaoRepository.findAll());

        // OUTROS
        model.addAttribute("listaPesquisas", pesquisaRepository.findAll());

        return "admin-painel";
    }

    @GetMapping("/admin/executar-procedure")
    @Transactional
    public String executarProcedure() {
        entityManager.createNativeQuery("CALL sp_verificar_integridade()").executeUpdate();
        return "redirect:/admin/painel?procedure=sucesso&tab=fauna";
    }

    @GetMapping("/admin/executar-feedback")
    @Transactional
    public String executarFeedback() {
        entityManager.createNativeQuery("CALL sp_classificar_feedback()").executeUpdate();
        return "redirect:/admin/painel?feedback=ok&tab=feedback";
    }

    // 🔥 NOVO - SIMULADOR
    @GetMapping("/admin/executar-simulacao")
    @Transactional
    public String executarSimulacao() {
        entityManager.createNativeQuery("CALL sp_classificar_simulacao()").executeUpdate();
        return "redirect:/admin/painel?simulacao=ok&tab=simulador";
    }
}