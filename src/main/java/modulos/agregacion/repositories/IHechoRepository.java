package modulos.agregacion.repositories;

import jakarta.transaction.Transactional;
import modulos.agregacion.entities.Hecho;
import modulos.agregacion.entities.Pais;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IHechoRepository extends JpaRepository<Hecho, Long>, JpaSpecificationExecutor<Hecho> {
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("""
   UPDATE Hecho h
   SET h.atributosHecho.modificado = false
   WHERE COALESCE(h.atributosHecho.modificado, false) = true
""")
    int resetAllModificado();
}
