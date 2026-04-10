package br.com.defeso.sistema.dao;

import br.com.defeso.sistema.domain.LogFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LogFeedbackRepository extends JpaRepository<LogFeedback, Long> {

    // 🔥 Buscar logs ordenados do mais recente para o mais antigo
    List<LogFeedback> findAllByOrderByDataExclusaoDesc();

}