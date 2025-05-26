package raiz.models.entities;

public enum Origen {
    CARGA_MANUAL(0),
    DATASET(1),
    CONTRIBUYENTE(2);

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
