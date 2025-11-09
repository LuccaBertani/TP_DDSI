import modulos.agregacion.entities.DbMain.UbicacionString;
import modulos.shared.utils.Geocodificador;

import java.util.Arrays;
import java.util.List;

public class TestDivertido {
    public static void main(String[] args) {
        // Lista de coordenadas de ejemplo (lat, lon)
        List<double[]> coordenadas = Arrays.asList(
                new double[]{-34.603722, -58.381592},   // Buenos Aires, Argentina
                new double[]{40.416775, -3.703790},     // Madrid, España
                new double[]{40.712776, -74.005974},    // Nueva York, USA
                new double[]{-33.448890, -70.669265},   // Santiago, Chile
                new double[]{35.689487, 139.691711}     // Tokio, Japón
        );

        for (double[] c : coordenadas) {
            Double lat = c[0];
            Double lon = c[1];

            System.out.printf("📍 Lat: %.6f | Lon: %.6f%n", lat, lon);
            UbicacionString ubicacion = Geocodificador.obtenerUbicacion(lat, lon);

            if (ubicacion != null) {
                System.out.println("  → País: " + ubicacion.getPais());
                System.out.println("  → Provincia/Estado: " + ubicacion.getProvincia());
            } else {
                System.out.println("  ❌ No se pudo obtener la ubicación");
            }

            System.out.println("-----------------------------------------");
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {} // delay para no saturar Nominatim
        }
    }
}