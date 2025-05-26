package raiz.models.entities.casosDeUso;

import raiz.models.entities.Coleccion;
import raiz.models.entities.Filtrador;
import raiz.models.entities.Hecho;
import raiz.models.entities.filtros.Filtro;

import java.util.List;

public class NavegarPorHechos {
    public void navegarPorHechos(List<Filtro> filtros, Coleccion coleccion){
        List<Hecho> lista = Filtrador.aplicarFiltros(filtros, coleccion.getHechos());

        for (Hecho hecho : lista){
            System.out.println(hecho.getTitulo());
        }
    }

    public void navegarPorHechos(Coleccion coleccion){
        for(Hecho hecho : coleccion.getHechos()){
            System.out.println(hecho.getTitulo());
        }
    }

}
