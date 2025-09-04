package modulos.agregacion.repositories;

import modulos.agregacion.entities.Categoria;
import modulos.agregacion.entities.CategoriaCantidad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICategoriaCantidadRepository extends JpaRepository<CategoriaCantidad, Long> {
}
