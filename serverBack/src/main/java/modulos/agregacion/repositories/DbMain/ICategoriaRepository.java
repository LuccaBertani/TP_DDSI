package modulos.agregacion.repositories.DbMain;

import modulos.agregacion.entities.DbMain.Categoria;
import modulos.agregacion.entities.DbMain.projections.CategoriaCantidadProjection;
import modulos.agregacion.entities.DbMain.projections.HoraCategoriaProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ICategoriaRepository extends JpaRepository<Categoria, Long> {


    @Query("""
SELECT c
FROM Categoria c
WHERE
  REPLACE(LOWER(c.titulo), ' ', '') =
  REPLACE(LOWER(:nombre), ' ', '')
  OR EXISTS (
    SELECT 1
    FROM Sinonimo s
    WHERE s MEMBER OF c.sinonimos
      AND REPLACE(LOWER(s.sinonimoStr), ' ', '') =
          REPLACE(LOWER(:nombre), ' ', '')
  )
""")
    Optional<Categoria> findByNombreNormalizado(@Param("nombre") String nombre);

}


