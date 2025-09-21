package modulos.agregacion.repositories;

import modulos.agregacion.entities.DbMain.solicitudes.SolicitudHecho;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ISolicitudAgregarHechoRepository extends JpaRepository<SolicitudHecho, Long> {
}