package modulos.agregacion.repositories;

import modulos.servicioEstadistica.entities.CategoriaCantidad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICategoriaCantidadRepository extends JpaRepository<CategoriaCantidad, Long> {
}
