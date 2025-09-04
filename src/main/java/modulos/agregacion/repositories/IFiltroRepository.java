package modulos.agregacion.repositories;

import modulos.agregacion.entities.filtros.Filtro;
import org.springframework.data.jpa.repository.JpaRepository;


// No necesito consultas específicas x cada tipo de filtro -> Solo hago repo para filtro genérico
// Permite findAll y findById
public interface IFiltroRepository extends JpaRepository<Filtro, Long> {
}