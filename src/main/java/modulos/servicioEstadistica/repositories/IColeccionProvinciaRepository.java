package modulos.servicioEstadistica.repositories;

import modulos.servicioEstadistica.entities.ColeccionProvincia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IColeccionProvinciaRepository extends JpaRepository<ColeccionProvincia, Long> {
}
