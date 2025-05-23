package models.entities.filtros;

import models.entities.Hecho;
import models.entities.TipoContenido;

public class FiltroContenidoMultimedia implements Filtro {

    TipoContenido tipoContenido;

    public FiltroContenidoMultimedia(TipoContenido tipoContenido) {
        this.tipoContenido = tipoContenido;
    }

    @Override
    public Boolean aprobarHecho(Hecho hecho) {
        return tipoContenido.equals(hecho.getContenidoMultimediaOpcional());
    }


}
