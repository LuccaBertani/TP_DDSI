public class FiltroContenidoMultimedia implements Filtro {
    TipoContenido tipoContenido;

    public Boolean aprobarHecho(Hecho hecho) {
        return tipoContenido == hecho.getContenidoMultimediaOpcional();
    }

    public FiltroContenidoMultimedia(TipoContenido tipoContenido) {
        this.tipoContenido = tipoContenido;
    }
}
