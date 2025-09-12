package modulos.agregacion.repositories;

import modulos.agregacion.entities.Ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface IUbicacionRepository extends JpaRepository<Ubicacion, Long> {
    @Query(value = """
    SELECT *
    FROM ubicacion u
    WHERE 
        ((:paisId IS NULL AND u.pais_id IS NULL) OR (:paisId IS NOT NULL AND u.pais_id = :paisId))
      AND
        ((:provinciaId IS NULL AND u.provincia_id IS NULL) OR (:provinciaId IS NOT NULL AND u.provincia_id = :provinciaId))
    LIMIT 1
""", nativeQuery = true) // El limit 1 porque sin querer ya guardé ubicaciones con fks repetidas
    Optional<Ubicacion> findByPaisIdAndProvinciaId(@Param("paisId") Long paisId,
                                                   @Param("provinciaId") Long provinciaId);


}
