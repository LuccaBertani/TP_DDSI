package modulos.agregacion.entities.atributosHecho;

public enum OrigenConexion {
    INVALIDO(-1),
    FRONT(0),
    PROXY(1);

    private final int codigo;

    OrigenConexion(int codigo) {
        this.codigo = codigo;
    }

    public int getCodigo() {
        return codigo;
    }

    public String codigoEnString() {
        return this.name();
    }

    public static OrigenConexion fromCodigo(int codigo) {
        for (OrigenConexion origen : OrigenConexion.values()) {
            if (origen.getCodigo() == codigo) {
                return origen;
            }
        }
        throw new IllegalArgumentException("Código de rol inválido: " + codigo);
    }
}
