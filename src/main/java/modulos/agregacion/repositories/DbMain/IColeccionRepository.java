package modulos.agregacion.repositories.DbMain;

import modulos.agregacion.entities.DbMain.Coleccion;
import modulos.agregacion.entities.DbMain.projections.ColeccionProvinciaProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IColeccionRepository extends JpaRepository<Coleccion, Long> {

    List<Coleccion> findByActivoTrueAndModificadoTrue();
    List<Coleccion> findByActivoTrue();

    @Query("""
    SELECT c FROM Coleccion c where c.activo = true
    """)
    List<Coleccion> findAllByActivoTrue();

    @Query("""
    SELECT c FROM Coleccion c where c.id = :id_coleccion
    """)
    Optional<Coleccion> findByIdAndActivoTrue(@Param("id_coleccion") Long idColeccion);
}