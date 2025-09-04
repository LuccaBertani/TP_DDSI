package modulos.agregacion.entities.filtros;

import jakarta.persistence.*;
import lombok.Getter;
import modulos.agregacion.entities.Hecho;
import modulos.agregacion.entities.TipoContenido;
@Getter

@Table(name = "filtro_contenido_multimedia")
@Entity
public class FiltroContenidoMultimedia extends Filtro {

    @Enumerated(EnumType.STRING)
    @Column(name = "tipoContenidoMultimedia", nullable = false, length = 20)
    TipoContenido tipoContenido;

    public FiltroContenidoMultimedia(TipoContenido tipoContenido) {
        this.tipoContenido = tipoContenido;
    }

    public FiltroContenidoMultimedia() {

    }

    @Override
    public Boolean aprobarHecho(Hecho hecho) {
        return tipoContenido.equals(hecho.getAtributosHecho().getContenidoMultimedia());
    }

}
