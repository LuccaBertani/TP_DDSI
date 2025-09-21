package modulos.agregacion.repositories;

import modulos.servicioEstadistica.entities.CategoriaProvincia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICategoriaProvinciaRepository extends JpaRepository<CategoriaProvincia, Long> {
}
