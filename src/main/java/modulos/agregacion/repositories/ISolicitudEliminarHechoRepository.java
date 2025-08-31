package modulos.agregacion.repositories;

import modulos.solicitudes.SolicitudHecho;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ISolicitudEliminarHechoRepository extends JpaRepository<SolicitudHecho, Long> {
}