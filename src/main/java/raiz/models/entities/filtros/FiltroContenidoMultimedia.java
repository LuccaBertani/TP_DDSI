package raiz.models.entities.filtros;

import raiz.models.entities.Hecho;
import raiz.models.entities.TipoContenido;

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
