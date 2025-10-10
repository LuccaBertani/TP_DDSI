package modulos.agregacion.repositories.DbMain;

import modulos.agregacion.entities.DbMain.hechoRef.HechoRef;
import modulos.agregacion.entities.DbMain.hechoRef.HechoRefKey;
import modulos.agregacion.entities.DbMain.projections.CategoriaCantidadProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IHechoRefRepository extends JpaRepository<HechoRef,Long> {
    @Query(value = """
        select hr from HechoRef hr
        where hr.key.fuente = :fuente
""")
    List<HechoRef> findByFuente(@Param("fuente") String fuente);
}
