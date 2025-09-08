package modulos.agregacion.repositories;

import modulos.agregacion.entities.projections.SolicitudHechoProjection;
import modulos.agregacion.entities.solicitudes.SolicitudHecho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ISolicitudRepository extends JpaRepository<SolicitudHecho, Long> {
    @Query(value = """
    SELECT 
        s.id AS id,
        s.id_usuario AS usuarioId,
        s.id_hecho AS hechoId,
        s.justificacion AS justificacion,
        s.procesada AS procesada,
        s.rechazada_por_spam AS rechazadaPorSpam
        
    FROM solicitud_hecho s
    WHERE s.procesada = false
""", nativeQuery = true)
    List<SolicitudHechoProjection> obtenerSolicitudesPendientes();
}
