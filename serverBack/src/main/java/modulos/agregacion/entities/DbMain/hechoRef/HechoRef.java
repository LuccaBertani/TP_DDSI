package modulos.agregacion.entities.DbMain.hechoRef;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import modulos.agregacion.entities.DbMain.Fuente;

@Entity
@Table(name = "hecho_ref")
@Getter
@Setter

public class HechoRef {

    @EmbeddedId
    private HechoRefKey key;

    public HechoRef(Long id, Fuente fuente) {
        this.key = new HechoRefKey(id,fuente);
    }

    public HechoRef() {

    }
}
