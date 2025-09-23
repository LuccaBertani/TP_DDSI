package modulos.agregacion.repositories.DbProxy;

import modulos.agregacion.entities.DbProxy.HechoProxy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IHechosProxyRepository extends JpaRepository<HechoProxy, Long> {
    @Query("""
SELECT h
FROM Hecho h 
WHERE REPLACE(LOWER(h.atributosHecho.titulo), ' ', '') =
      REPLACE(LOWER(:nombre), ' ', '')
""")
    List<HechoProxy> findAllByNombreNormalizado(@Param("nombre") String nombre);

    @Query("""
SELECT h
FROM Hecho h 
WHERE REPLACE(LOWER(h.atributosHecho.titulo), ' ', '') =
      REPLACE(LOWER(:nombre), ' ', '')
""")
    Optional<HechoProxy> findByNombreNormalizado(@Param("nombre") String nombre);
}