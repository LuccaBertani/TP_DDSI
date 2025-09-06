package modulos.agregacion.repositories;

import modulos.agregacion.entities.Categoria;
import modulos.agregacion.entities.Pais;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface IPaisRepository extends JpaRepository<Pais, Long> {
    @Query(value = """
    SELECT p
    FROM Pais p 
    WHERE unaccent(REPLACE(LOWER(p.pais), ' ', '')) = unaccent(REPLACE(LOWER(:nombre), ' ', ''))
""")
    Optional<Pais> findByNombreNormalizado(@Param("nombre") String nombre);
}
