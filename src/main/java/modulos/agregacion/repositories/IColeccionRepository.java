package modulos.agregacion.repositories;

import modulos.agregacion.entities.CategoriaCantidad;
import modulos.agregacion.entities.Coleccion;
import modulos.agregacion.entities.ColeccionProvincia;
import modulos.agregacion.entities.projections.ColeccionProvinciaProjection;
import org.intellij.lang.annotations.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface IColeccionRepository extends JpaRepository<Coleccion, Long> {
    @Query(value = """
        SELECT
            p.id      AS provinciaId,
            COUNT(*)  AS totalHechos,
            c.id      AS coleccionId
        FROM coleccion c
        JOIN coleccion_hecho ch ON ch.coleccion_id = c.id
        JOIN hecho h           ON h.id = ch.hecho_id
        JOIN ubicacion u       ON u.id = h.ubicacion_id
        JOIN provincia p       ON p.id = u.provincia_id
        GROUP BY p.id, c.id
        ORDER BY totalHechos DESC
        LIMIT 1
        """, nativeQuery = true)
    List<ColeccionProvinciaProjection> obtenerMayorCantHechosProvinciaEnColeccion();

    List<Coleccion> findByActivoTrueAndModificadoTrue();
    List<Coleccion> findByActivoTrue();
}