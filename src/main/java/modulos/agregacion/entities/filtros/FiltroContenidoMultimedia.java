package modulos.agregacion.entities.filtros;

import lombok.Getter;
import modulos.shared.Hecho;
import modulos.shared.TipoContenido;
@Getter
public class FiltroContenidoMultimedia implements Filtro {

    TipoContenido tipoContenido;

    public FiltroContenidoMultimedia(TipoContenido tipoContenido) {
        this.tipoContenido = tipoContenido;
    }

    @Override
    public Boolean aprobarHecho(Hecho hecho) {
        return tipoContenido.equals(hecho.getAtributosHecho().getContenidoMultimedia());
    }


}
