package br.com.defeso.sistema.dao;

import br.com.defeso.sistema.domain.Pesquisa;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; // Import necessário para a lista

public interface PesquisaRepository extends JpaRepository<Pesquisa, Long> {

    // Método mágico do Spring para buscar pesquisas pelo ID do usuário vinculado
    List<Pesquisa> findByUsuarioId(Long usuarioId);

}