package modulos.agregacion.repositories;

import modulos.agregacion.entities.DbDinamica.HechoDinamica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IHechosDinamicaRepository extends JpaRepository<HechoDinamica, Long> {

    @Query("""
  select h
  from HechoDinamica h
  where h.id = :idHecho
    and h.usuario is not null
    and h.usuario.id = :idUsuario
""")
    Optional<HechoDinamica> findByIdAndUsuario(@Param("idHecho") Long idHecho, @Param("idUsuario") Long idUsuario);

    @Query("""
SELECT h
FROM Hecho h 
WHERE REPLACE(LOWER(h.atributosHecho.titulo), ' ', '') =
      REPLACE(LOWER(:nombre), ' ', '')
""")
    List<HechoDinamica> findAllByNombreNormalizado(@Param("nombre") String nombre);

    @Query("""
SELECT h
FROM Hecho h 
WHERE REPLACE(LOWER(h.atributosHecho.titulo), ' ', '') =
      REPLACE(LOWER(:nombre), ' ', '')
""")
    Optional<HechoDinamica> findByNombreNormalizado(@Param("nombre") String nombre);

}