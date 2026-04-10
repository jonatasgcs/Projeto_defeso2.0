package br.com.defeso.sistema.dao;

import br.com.defeso.sistema.domain.LogSimulacao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LogSimulacaoRepository extends JpaRepository<LogSimulacao, Long> {

    // 🔥 Lista os logs do mais recente para o mais antigo
    List<LogSimulacao> findAllByOrderByDataExclusaoDesc();

}