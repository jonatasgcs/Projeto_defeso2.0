package br.com.defeso.sistema.dao;

import br.com.defeso.sistema.domain.LogFauna;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogFaunaRepository extends JpaRepository<LogFauna, Long> {

    // Retorna os logs mais recentes primeiro (usado na auditoria do Admin)
    List<LogFauna> findAllByOrderByDataExclusaoDesc();
}