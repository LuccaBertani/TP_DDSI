package modulos.agregacion.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import modulos.agregacion.entities.DbMain.algoritmosConsenso.AlgoritmoConsensoMayoriaAbsoluta;
import modulos.agregacion.entities.DbMain.algoritmosConsenso.AlgoritmoConsensoMayoriaSimple;
import modulos.agregacion.entities.DbMain.algoritmosConsenso.AlgoritmoConsensoMultiplesMenciones;
import modulos.agregacion.entities.DbMain.algoritmosConsenso.IAlgoritmoConsenso;

@Converter(autoApply = true)
public class AlgoritmoConsensoConverter implements AttributeConverter<IAlgoritmoConsenso, String> {

    private static final String COD_MS  = "MAYORIA_SIMPLE";
    private static final String COD_MA  = "MAYORIA_ABSOLUTA";
    private static final String COD_MM  = "MULTIPLES_MENCIONES";

    @Override
    public String convertToDatabaseColumn(IAlgoritmoConsenso atributo) {
        if (atributo == null) return null;

        if (atributo instanceof AlgoritmoConsensoMayoriaSimple)    return COD_MS;
        if (atributo instanceof AlgoritmoConsensoMayoriaAbsoluta)  return COD_MA;
        if (atributo instanceof AlgoritmoConsensoMultiplesMenciones) return COD_MM;

        throw new IllegalArgumentException("AlgoritmoConsenso desconocido: " + atributo.getClass());
    }

    @Override
    public IAlgoritmoConsenso convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) return null;
        String code = dbData.trim().toUpperCase();

        switch (code) {
            case COD_MS: return new AlgoritmoConsensoMayoriaSimple();
            case COD_MA: return new AlgoritmoConsensoMayoriaAbsoluta();
            case COD_MM: return new AlgoritmoConsensoMultiplesMenciones();
            default:
                throw new IllegalArgumentException("Código de algoritmo inválido en BD: " + dbData);
        }
    }
}

