package models.entities.casosDeUso;

import models.entities.Coleccion;
import models.entities.Filtrador;
import models.entities.Hecho;
import models.entities.filtros.Filtro;

import java.util.List;

public class NavegarPorHechos {
    public void navegarPorHechos(List<Filtro> filtros, Coleccion coleccion){
        Filtrador filtrador = new Filtrador();
        List<Hecho> lista = filtrador.aplicarFiltros(filtros, coleccion.getHechos());

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
