package modulos.agregacion.repositories.DbDinamica;

import modulos.agregacion.entities.DbDinamica.solicitudes.SolicitudHecho;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ISolicitudAgregarHechoRepository extends JpaRepository<SolicitudHecho, Long> {
}