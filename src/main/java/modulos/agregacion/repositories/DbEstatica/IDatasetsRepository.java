package modulos.agregacion.repositories.DbEstatica;

import modulos.agregacion.entities.DbEstatica.Dataset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IDatasetsRepository extends JpaRepository<Dataset, Long> {
}