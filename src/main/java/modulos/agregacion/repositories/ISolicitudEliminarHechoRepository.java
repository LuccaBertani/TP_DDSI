package modulos.agregacion.repositories;

import modulos.agregacion.entities.solicitudes.SolicitudHecho;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ISolicitudEliminarHechoRepository extends JpaRepository<SolicitudHecho, Long> {
}