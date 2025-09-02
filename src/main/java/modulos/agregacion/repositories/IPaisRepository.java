package modulos.agregacion.repositories;

import modulos.agregacion.entities.Pais;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPaisRepository extends JpaRepository<Pais, Long> {
}
