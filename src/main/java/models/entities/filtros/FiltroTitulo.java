package models.entities.filtros;

import lombok.Getter;
import lombok.Setter;
import models.entities.Hecho;
import models.entities.Normalizador;

import java.util.Arrays;
import java.util.List;

public class FiltroTitulo implements Filtro {
    @Getter
    @Setter
    String titulo;

    public FiltroTitulo(String titulo) {
        this.titulo = titulo;
    }

    @Override
    public Boolean aprobarHecho(Hecho hecho) {

        List<String> palabrasHecho = Arrays.stream(Normalizador.normalizarSeparado(hecho.getTitulo()))
                .map(String::trim)
                .toList();

        List<String> palabrasFiltro = Arrays.stream(Normalizador.normalizar(this.titulo))
                .map(String::trim)
                .toList();


        // Si la descripcion del hecho enviado por parametro tiene todas sus palabras contenidas en el filtro de la descripcion
        return palabrasHecho.containsAll(palabrasFiltro);
    }
}