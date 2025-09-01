package modulos.agregacion.repositories;

import modulos.agregacion.entities.fuentes.Dataset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IDatasetsRepository extends JpaRepository<Dataset, Long> {
}