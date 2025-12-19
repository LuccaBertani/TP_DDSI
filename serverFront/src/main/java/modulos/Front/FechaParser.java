package modulos.Front;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class FechaParser {

    private static final List<DateTimeFormatter> FORMATOS_LOCAL = Arrays.asList(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ROOT),
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss", Locale.ROOT),
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss", Locale.ROOT),
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss", Locale.ROOT),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm", Locale.ROOT),
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ROOT),
            DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ROOT),
            DateTimeFormatter.ofPattern("dd/M/yyyy", Locale.ROOT),
            DateTimeFormatter.ofPattern("d/M/yyyy", Locale.ROOT),
            DateTimeFormatter.ofPattern("d/MM/yyyy", Locale.ROOT),
            DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ROOT),
            DateTimeFormatter.ofPattern("d-M-yyyy", Locale.ROOT)
    );

    private static final List<DateTimeFormatter> FORMATOS_ZONED = Arrays.asList(
            DateTimeFormatter.ISO_ZONED_DATE_TIME,    // 2023-10-05T10:15:30+01:00[Europe/Paris]
            DateTimeFormatter.ISO_OFFSET_DATE_TIME    // 2023-10-05T10:15:30+01:00
    );

    public static LocalDateTime parsearFecha(String fechaStr) {
        if (fechaStr == null || fechaStr.trim().isEmpty()) {
            return null;
        }

        fechaStr = fechaStr.trim();

        for (DateTimeFormatter formatter : FORMATOS_ZONED) {
            try {
                ZonedDateTime zdt = ZonedDateTime.parse(fechaStr, formatter);
                return zdt.toLocalDateTime();
            } catch (DateTimeParseException ignored) {
            }
        }
        for (DateTimeFormatter formatter : FORMATOS_LOCAL) {
            try {
                return LocalDateTime.parse(fechaStr, formatter);
            } catch (DateTimeParseException ignored) {
            }

            try {
                LocalDate date = LocalDate.parse(fechaStr, formatter);
                return date.atStartOfDay();
            } catch (DateTimeParseException ignored) {
            }
        }

        return null;
    }

    public static boolean sonMismaFecha(LocalDateTime fecha1, LocalDateTime fecha2) {
        if (fecha1 == null || fecha2 == null) return false;
        return fecha1.getDayOfMonth() == fecha2.getDayOfMonth()
                && fecha1.getMonthValue() == fecha2.getMonthValue()
                && fecha1.getYear() == fecha2.getYear();
    }


    public static void main(String[] args) {
        String[] fechasDePrueba = {
                "2000-01-01T00:00",
                "2023-10-05T10:15:30+01:00[Europe/Paris]",
                "2023-10-05T10:15:30+01:00",
                "2023-12-01 14:30:00",
                "1/5/2017",
                "01/05/2017",
                "5/1/2017",
                "05/01/2017",
                "2023-10-05",
                "2023-10-05T10:15:30" // ISO con segundos
        };

        DateTimeFormatter formatterSalida = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss", Locale.ROOT);

        System.out.println("--- PRUEBAS DE FechaParser ---");
        for (String fecha : fechasDePrueba) {
            try {
                LocalDateTime ldt = parsearFecha(fecha);
                String parsedOutput = (ldt != null) ? ldt.format(formatterSalida) : "null";
                System.out.println("Entrada: " + fecha + " -> Parsed: " + parsedOutput);
            } catch (Exception e) {
                System.out.println("Entrada: " + fecha + " -> ERROR EN PARSEADO: " + e.getMessage());
            }
        }
        System.out.println("-----------------------------");
    }
}