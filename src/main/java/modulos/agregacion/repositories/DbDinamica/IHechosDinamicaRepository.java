package modulos.agregacion.repositories.DbDinamica;

import modulos.agregacion.entities.DbDinamica.HechoDinamica;
import modulos.agregacion.entities.DbMain.Hecho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/*

@Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("""
   UPDATE Hecho h
   SET h.atributosHecho.modificado = false
   WHERE COALESCE(h.atributosHecho.modificado, false) = true
""")
    int resetAllModificado();

*/

public interface IHechosDinamicaRepository extends JpaRepository<HechoDinamica, Long>, JpaSpecificationExecutor<HechoDinamica> {

    @Query("""
    select h
    from HechoDinamica h
    where h.id = :idHecho
    and h.usuario_id = :idUsuario
""")
    Optional<HechoDinamica> findByIdAndUsuario(@Param("idHecho") Long idHecho, @Param("idUsuario") Long idUsuario);

    @Query("""
SELECT h
FROM HechoDinamica h 
WHERE REPLACE(LOWER(h.atributosHecho.titulo), ' ', '') =
      REPLACE(LOWER(:nombre), ' ', '')
""")
    List<HechoDinamica> findAllByNombreNormalizado(@Param("nombre") String nombre);

    @Query("""
SELECT h
FROM HechoDinamica h 
WHERE REPLACE(LOWER(h.atributosHecho.titulo), ' ', '') =
      REPLACE(LOWER(:nombre), ' ', '')
""")
    Optional<HechoDinamica> findByNombreNormalizado(@Param("nombre") String nombre);

}