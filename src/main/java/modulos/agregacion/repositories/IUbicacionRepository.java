package modulos.agregacion.repositories;

import modulos.agregacion.entities.Ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface IUbicacionRepository extends JpaRepository<Ubicacion, Long> {
    @Query("""
        select u
        from Ubicacion u
        where u.pais.id = :paisId
          and u.provincia.id = :provinciaId
    """)
    Optional<Ubicacion> findByPaisIdAndProvinciaId(@Param("paisId") Long paisId,
                                                   @Param("provinciaId") Long provinciaId);
}
