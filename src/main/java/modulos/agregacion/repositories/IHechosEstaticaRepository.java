package modulos.agregacion.repositories;

import modulos.agregacion.entities.Hecho;
import modulos.agregacion.entities.HechoEstatica;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IHechosEstaticaRepository extends JpaRepository<HechoEstatica, Long> {
}