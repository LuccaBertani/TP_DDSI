package modulos.agregacion.repositories.DbDinamica;

import modulos.agregacion.entities.DbMain.projections.CantSolicitudesSpamProjection;
import modulos.agregacion.entities.DbDinamica.solicitudes.SolicitudHecho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ISolicitudEliminarHechoRepository extends JpaRepository<SolicitudHecho, Long> {

    @Query(value = """
    SELECT COUNT(*) as total_spam
    FROM solicitud_hecho AS s
    WHERE s.rechazada_por_spam = 1""",nativeQuery = true)
    CantSolicitudesSpamProjection obtenerCantSolicitudesEliminacionSpam();



}