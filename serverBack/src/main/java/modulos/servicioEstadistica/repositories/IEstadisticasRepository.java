package modulos.servicioEstadistica.repositories;

import modulos.servicioEstadistica.entities.Estadisticas;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IEstadisticasRepository extends JpaRepository<Estadisticas, Long> {
}
