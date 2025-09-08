package modulos.agregacion.repositories;

import modulos.agregacion.entities.Hecho;
import modulos.agregacion.entities.HechoDinamica;
import modulos.agregacion.entities.HechoProxy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IHechosProxyRepository extends JpaRepository<HechoProxy, Long> {
    @Query(value = """
    SELECT h
    FROM Hecho h 
    WHERE unaccent(REPLACE(LOWER(h.atributosHecho.titulo), ' ', '')) = unaccent(REPLACE(LOWER(:nombre), ' ', ''))
""")
    Optional<HechoProxy> findByNombreNormalizado(@Param("nombre") String nombre);

    @Query(value = """
    SELECT h
    FROM Hecho h 
    WHERE unaccent(REPLACE(LOWER(h.atributosHecho.titulo), ' ', '')) = unaccent(REPLACE(LOWER(:nombre), ' ', ''))
""")
    List<HechoProxy> findAllByNombreNormalizado(@Param("nombre") String nombre);

}