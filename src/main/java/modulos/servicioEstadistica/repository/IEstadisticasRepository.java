package modulos.servicioEstadistica.repository;

import modulos.servicioEstadistica.Entidad.Estadisticas;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IEstadisticasRepository extends JpaRepository<Estadisticas, Long> {
}
