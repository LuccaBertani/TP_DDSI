package modulos.agregacion.repositories;

import modulos.agregacion.entities.ColeccionProvincia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IColeccionProvinciaRepository extends JpaRepository<ColeccionProvincia, Long> {
}
