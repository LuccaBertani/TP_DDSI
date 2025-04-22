import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class VisualizadorTest {
    @Test
    public void a(){
        Visualizador visualizador = new Visualizador();
        FiltroTitulo filtroTitulo = new FiltroTitulo("Incendio");

        Hecho hecho = new Hecho();
        hecho.setTitulo("cuack");
        Pais arg = new Pais();
        arg.setPais("Argentina");
        hecho.setPais(arg);

        Categoria categoria = new Categoria();
        categoria.setTitulo("supercategoria inventada");
        hecho.setCategoria(categoria);
        Globales.hechosTotales.add(hecho);

        FiltroPais filtroPais = new FiltroPais(arg);

        List<Filtro> filtros = new ArrayList<>();
        filtros.add(filtroTitulo);
        filtros.add(filtroPais);
        FuenteEstatica fuenteEstatica = new FuenteEstatica();
        fuenteEstatica.setDataSet("C:\\Users\\nehue\\Downloads\\Libro2.csv");


        List<Hecho> hechos = fuenteEstatica.leerFuente();
        //hechos.add(hecho);
        visualizador.subirHechos(hechos);


        Set<Hecho> hechosFiltrados = visualizador.aplicarFiltros(filtros);

        System.out.println("LENGTH HECHOS FILTRADOS: " + hechosFiltrados.size());
        for (Hecho h : hechosFiltrados){
            System.out.println(h.getTitulo());
            /*System.out.println(h.getDescripcion());
            System.out.println(h.getCategoria().getTitulo());
            System.out.println(h.getPais().getPais());
            System.out.println(h.getFechaAcontecimiento());
            System.out.println(h.getFechaDeCarga());*/
        }

    }


}