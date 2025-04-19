import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FuenteEstaticaTest {
    @Test
    public void testExcel(){
        FuenteEstatica fuenteEstatica = new FuenteEstatica();
        fuenteEstatica.setDataSet("C:\\Users\\nehue\\Downloads\\Libro2.csv");

        List<Hecho> hechos = fuenteEstatica.leerFuente();


        for (Hecho hecho : hechos){
            System.out.println(hecho.getTitulo());
            System.out.println(hecho.getDescripcion());
            System.out.println(hecho.getCategoria().getTitulo());
            System.out.println(hecho.getPais().getPais());
            System.out.println(hecho.getFechaAcontecimiento());
            System.out.println(hecho.getFechaDeCarga());
        }






    }

}