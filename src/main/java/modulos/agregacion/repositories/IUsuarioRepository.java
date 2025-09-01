package modulos.agregacion.repositories;

import modulos.agregacion.entities.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUsuarioRepository extends JpaRepository<Usuario, Long> {
}