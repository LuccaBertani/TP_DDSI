package modulos.agregacion.entities.filtros;

import modulos.shared.Hecho;
import modulos.shared.TipoContenido;

public class FiltroContenidoMultimedia implements Filtro {

    TipoContenido tipoContenido;

    public FiltroContenidoMultimedia(TipoContenido tipoContenido) {
        this.tipoContenido = tipoContenido;
    }

    @Override
    public Boolean aprobarHecho(Hecho hecho) {
        return tipoContenido.equals(hecho.getContenidoMultimedia());
    }


}
