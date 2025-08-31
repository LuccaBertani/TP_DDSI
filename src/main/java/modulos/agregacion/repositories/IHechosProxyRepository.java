package modulos.agregacion.repositories;

import modulos.shared.Hecho;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IHechosProxyRepository extends JpaRepository<Hecho, Long> {
}