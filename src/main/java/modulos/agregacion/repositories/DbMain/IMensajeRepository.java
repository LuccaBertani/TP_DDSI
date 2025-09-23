package modulos.agregacion.repositories.DbMain;

import modulos.agregacion.entities.DbDinamica.solicitudes.Mensaje;
import modulos.agregacion.entities.DbMain.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IMensajeRepository extends JpaRepository<Mensaje, Long> {
    List<Mensaje> findByReceptor(Usuario receptor);
}
