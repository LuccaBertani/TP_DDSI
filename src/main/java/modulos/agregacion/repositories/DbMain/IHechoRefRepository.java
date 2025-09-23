package modulos.agregacion.repositories.DbMain;

import modulos.agregacion.entities.DbMain.HechoRef;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IHechoRefRepository extends JpaRepository<HechoRef,Long> {
}
