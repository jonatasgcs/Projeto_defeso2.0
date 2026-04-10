package br.com.defeso.sistema.dao; // <--- Mudamos para dao

import br.com.defeso.sistema.domain.Usuario; // <--- Importa da pasta domain
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
}