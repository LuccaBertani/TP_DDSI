package modulos.agregacion.repositories;

import modulos.agregacion.entities.Ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUbicacionRepository extends JpaRepository<Ubicacion, Long> {
}
