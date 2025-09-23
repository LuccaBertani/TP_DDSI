package modulos.agregacion.repositories.DbMain;

import modulos.agregacion.entities.DbMain.solicitudes.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IReporteHechoRepository extends JpaRepository<Reporte, Long> {
}