package modulos.agregacion.repositories;

import modulos.agregacion.entities.Categoria;
import modulos.agregacion.entities.CategoriaProvincia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICategoriaProvinciaRepository extends JpaRepository<CategoriaProvincia, Long> {
}
