package modulos.agregacion.repositories;

import modulos.agregacion.entities.Hecho;
import modulos.agregacion.entities.HechoDinamica;
import modulos.agregacion.entities.HechoEstatica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IHechosEstaticaRepository extends JpaRepository<HechoEstatica, Long> {
    @Query("""
SELECT h
FROM Hecho h 
WHERE REPLACE(LOWER(h.atributosHecho.titulo), ' ', '') =
      REPLACE(LOWER(:nombre), ' ', '')
""")
    List<HechoEstatica> findAllByNombreNormalizado(@Param("nombre") String nombre);

    @Query("""
SELECT h
FROM Hecho h 
WHERE REPLACE(LOWER(h.atributosHecho.titulo), ' ', '') =
      REPLACE(LOWER(:nombre), ' ', '')
""")
    Optional<HechoEstatica> findByNombreNormalizado(@Param("nombre") String nombre);
}