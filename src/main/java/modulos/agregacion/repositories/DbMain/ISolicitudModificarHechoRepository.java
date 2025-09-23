package modulos.agregacion.repositories.DbMain;

import modulos.agregacion.entities.DbMain.solicitudes.SolicitudHecho;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ISolicitudModificarHechoRepository extends JpaRepository<SolicitudHecho, Long> {
}