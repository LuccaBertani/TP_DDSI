package modulos.agregacion.repositories;

import modulos.servicioEstadistica.entities.CategoriaHora;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICategoriaHoraRepository extends JpaRepository<CategoriaHora, Long> {
}
