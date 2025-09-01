package modulos.agregacion.repositories;

import modulos.agregacion.entities.solicitudes.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IReporteHechoRepository extends JpaRepository<Reporte, Long> {
}