import raiz.models.entities.Geocodificador;
import org.junit.jupiter.api.Test;

class GeocodificadorTest {
    @Test
    public void testearCodificador(){
        System.out.println(Geocodificador.obtenerPais(-34.603722,-58.381592));

    }
}