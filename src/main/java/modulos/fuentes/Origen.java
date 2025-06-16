package modulos.fuentes;

public enum Origen {
    FUENTE_DINAMICA(0),
    FUENTE_ESTATICA(1),
    CARGA_MANUAL(2),
    FUENTE_PROXY_METAMAPA(3);

    private final int codigo;

    Origen(int codigo) {
        this.codigo = codigo;
    }

    public int getCodigo() {
        return codigo;
    }

    public static Origen fromCodigo(int codigo) {
        for (Origen origen : Origen.values()) {
            if (origen.getCodigo() == codigo) {
                return origen;
            }
        }
        throw new IllegalArgumentException("Código de rol inválido: " + codigo);
    }

}
