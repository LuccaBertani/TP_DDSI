package modulos.agregacion.repositories;

import modulos.agregacion.entities.DbMain.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IUsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByNombreDeUsuario(String username);
    boolean existsByNombreDeUsuario(String username);
}