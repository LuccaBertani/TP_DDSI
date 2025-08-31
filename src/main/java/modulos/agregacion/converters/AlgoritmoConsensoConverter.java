package modulos.agregacion.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import modulos.agregacion.entities.algoritmosConsenso.AlgoritmoConsensoMayoriaAbsoluta;
import modulos.agregacion.entities.algoritmosConsenso.AlgoritmoConsensoMayoriaSimple;
import modulos.agregacion.entities.algoritmosConsenso.AlgoritmoConsensoMultiplesMenciones;
import modulos.agregacion.entities.algoritmosConsenso.IAlgoritmoConsenso;

@Converter(autoApply = true)
public class AlgoritmoConsensoConverter implements AttributeConverter<IAlgoritmoConsenso, String>{


        @Override
        public String convertToDatabaseColumn(IAlgoritmoConsenso atributo) {
            if (atributo == null) return null;
            return atributo.getClass().getSimpleName(); // guarda "AlgoritmoConsensoMayoriaSimple" etc.
        }

        @Override
        public IAlgoritmoConsenso convertToEntityAttribute(String dbData) {
            switch (dbData) {
                case "AlgoritmoConsensoMayoriaAbsoluta": return new AlgoritmoConsensoMayoriaAbsoluta();
                case "AlgoritmoConsensoMayoriaSimple": return new AlgoritmoConsensoMayoriaSimple();
                case "AlgoritmoConsensoMultiplesMenciones": return new AlgoritmoConsensoMultiplesMenciones();
                default: return null;
            }
        }
}

