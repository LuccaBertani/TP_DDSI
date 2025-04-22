import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
//Filtro contenido en Titulo. Manzana. Manzana podrida, Manzana sana.
class VisualizadorTest {
    @Test
    public void a(){
        Visualizador visualizador = new Visualizador();
        FiltroTitulo filtroTitulo = new FiltroTitulo("Incendio");
        List<Filtro> filtros = new ArrayList<>();
        filtros.add(filtroTitulo);
        FuenteEstatica fuenteEstatica = new FuenteEstatica();
        fuenteEstatica.setDataSet("C:\\Users\\nehue\\Downloads\\Libro2.csv");

        fuenteEstatica.leerFuente();

        int n = 1;
        for (Hecho hecho : Globales.hechosTotales){
            System.out.println("Hecho nro: " + n);

            System.out.println(hecho.getTitulo());
            System.out.println(hecho.getDescripcion());
            System.out.println(hecho.getCategoria().getTitulo());
            System.out.println(hecho.getPais().getPais());
            System.out.println(hecho.getFechaAcontecimiento());
            System.out.println(hecho.getFechaDeCarga());

            n++;
            System.out.println("-----------");
        }

        //visualizador.navegarPorHechos(filtros);

    }



}