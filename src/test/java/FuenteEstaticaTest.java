import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FuenteEstaticaTest {
    @Test
    public void testExcel(){
        FuenteEstatica fuenteEstatica = new FuenteEstatica();
        fuenteEstatica.setDataSet("C:\\Users\\nehue\\Downloads\\Libro2.csv");

        fuenteEstatica.leerFuente();



    }

}