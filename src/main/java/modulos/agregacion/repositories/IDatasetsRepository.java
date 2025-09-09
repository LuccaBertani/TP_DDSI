package modulos.agregacion.repositories;

import modulos.agregacion.entities.Hecho;
import modulos.agregacion.entities.fuentes.Dataset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IDatasetsRepository extends JpaRepository<Dataset, Long> {
}