package modulos.agregacion.entities;

public enum TipoSolicitud {
    SOLICITUD_AGREGAR(0),
    SOLICITUD_ELIMINAR(1),
    SOLICITUD_MODIFICAR(2);

    private final int codigo;

    TipoSolicitud(int codigo) {
    this.codigo = codigo;
    }

    public int getCodigo() {
        return codigo;
    }

}
