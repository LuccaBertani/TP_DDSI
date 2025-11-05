package modulos.agregacion.repositories.DbDinamica;

import modulos.agregacion.entities.DbMain.projections.SolicitudHechoProjection;
import modulos.agregacion.entities.DbDinamica.solicitudes.SolicitudHecho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ISolicitudRepository extends JpaRepository<SolicitudHecho, Long> {
    @Query(value = """
    SELECT 
        s.id AS id,
        s.id_usuario AS usuarioId,
        s.id_hecho AS hechoId,
        s.justificacion AS justificacion,
        s.procesada AS procesada,
        s.rechazadaPorSpam AS rechazadaPorSpam
        
    FROM solicitud_hecho s
    WHERE s.procesada = false
""", nativeQuery = true)
    List<SolicitudHechoProjection> obtenerSolicitudesPendientes();

    @Query("""
    SELECT s FROM SolicitudHecho s WHERE s.procesada = false AND s.id = :id_solicitud
    """)
    Optional<SolicitudHecho> findByIdAndProcesadaFalse(@Param("id_solicitud") Long idSolicitud);

    @Query(value = """
            SELECT ifnull((COUNT(*) * 100.0 / (SELECT COUNT(*) FROM solicitud_hecho)) ,0)
                FROM solicitud_hecho
                WHERE procesada = true
    """, nativeQuery = true)
    Double porcentajeProcesadas();

}
