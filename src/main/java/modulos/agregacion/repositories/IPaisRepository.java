package modulos.agregacion.repositories;

import modulos.agregacion.entities.Categoria;
import modulos.agregacion.entities.Pais;
import modulos.agregacion.entities.Provincia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface IPaisRepository extends JpaRepository<Pais, Long> {
    @Query("""
SELECT p
FROM Pais p
WHERE
  REPLACE(LOWER(FUNCTION('unaccent', p.pais)), ' ', '') =
  REPLACE(LOWER(FUNCTION('unaccent', :nombre)), ' ', '')
  OR EXISTS (
    SELECT 1
    FROM Sinonimo s
    WHERE s MEMBER OF p.sinonimos
      AND REPLACE(LOWER(FUNCTION('unaccent', s.sinonimoStr)), ' ', '') =
          REPLACE(LOWER(FUNCTION('unaccent', :nombre)),        ' ', '')
  )
""")
    Optional<Pais> findByNombreNormalizado(@Param("nombre") String nombre);
}
