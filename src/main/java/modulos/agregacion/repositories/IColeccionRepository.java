package modulos.agregacion.repositories;

import modulos.agregacion.entities.Coleccion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IColeccionRepository extends JpaRepository<Coleccion, Long> {
}
