package modulos.agregacion.repositories;

import modulos.agregacion.entities.Hecho;
import modulos.agregacion.entities.HechoDinamica;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IHechosDinamicaRepository extends JpaRepository<HechoDinamica, Long> {
}