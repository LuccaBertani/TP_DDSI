package modulos.agregacion.repositories;

import modulos.fuentes.Dataset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IDatasetsRepository extends JpaRepository<Dataset, Long> {
}