package modulos.agregacion.repositories;

import modulos.solicitudes.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IMensajeRepository extends JpaRepository<Mensaje, Long> {
}
