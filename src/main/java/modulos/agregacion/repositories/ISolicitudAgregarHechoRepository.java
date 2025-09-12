package modulos.agregacion.repositories;

import jakarta.validation.constraints.NotNull;
import modulos.agregacion.entities.solicitudes.SolicitudHecho;
import modulos.agregacion.entities.solicitudes.SolicitudSubirHecho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ISolicitudAgregarHechoRepository extends JpaRepository<SolicitudHecho, Long> {
}