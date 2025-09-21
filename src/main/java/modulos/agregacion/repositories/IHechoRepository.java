package modulos.agregacion.repositories;

import jakarta.transaction.Transactional;
import modulos.agregacion.entities.DbMain.Hecho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IHechoRepository extends JpaRepository<Hecho, Long>, JpaSpecificationExecutor<Hecho> {
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("""
   UPDATE Hecho h
   SET h.atributosHecho.modificado = false
   WHERE COALESCE(h.atributosHecho.modificado, false) = true
""")
    int resetAllModificado();


    @Query("""
        select distinct h
        from Coleccion c
        join c.hechos h
        join h.datasets d
        where c.id = :coleccionId
          and d.id = :datasetId
    """)
    List<Hecho> findHechosByColeccionAndDataset(@Param("coleccionId") Long coleccionId,
                                                @Param("datasetId") Long datasetId);

}
