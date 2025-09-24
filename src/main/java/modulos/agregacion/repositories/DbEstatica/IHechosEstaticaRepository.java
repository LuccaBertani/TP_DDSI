package modulos.agregacion.repositories.DbEstatica;

import modulos.agregacion.entities.DbEstatica.HechoEstatica;
import modulos.agregacion.entities.DbMain.Hecho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IHechosEstaticaRepository extends JpaRepository<HechoEstatica, Long>, JpaSpecificationExecutor<HechoEstatica> {
    @Query("""
SELECT h
FROM HechoEstatica h 
WHERE REPLACE(LOWER(h.atributosHecho.titulo), ' ', '') =
      REPLACE(LOWER(:nombre), ' ', '')
""")
    List<HechoEstatica> findAllByNombreNormalizado(@Param("nombre") String nombre);

    @Query("""
SELECT h
FROM HechoEstatica h 
WHERE REPLACE(LOWER(h.atributosHecho.titulo), ' ', '') =
      REPLACE(LOWER(:nombre), ' ', '')
""")
    Optional<HechoEstatica> findByNombreNormalizado(@Param("nombre") String nombre);

    @Query("""
        select h
        from HechoEstatica h
        join Dataset d 
            where d.id = 
    """)
    List<HechoEstatica> findHechosByDataset(@Param("datasetId") Long datasetId);

}