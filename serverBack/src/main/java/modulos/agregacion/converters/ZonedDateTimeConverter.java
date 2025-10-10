package modulos.agregacion.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import modulos.shared.utils.FechaParser;

import java.time.ZonedDateTime;

@Converter(autoApply = true)
public class ZonedDateTimeConverter implements AttributeConverter<ZonedDateTime, String> {
    @Override
    public String convertToDatabaseColumn(ZonedDateTime atributo) {
        if (atributo == null) return null;
        return atributo.toString();
    }

    @Override
    public ZonedDateTime convertToEntityAttribute(String dbData) {
        if (dbData == null)
            return null;
        return FechaParser.parsearFecha(dbData);
    }
}
