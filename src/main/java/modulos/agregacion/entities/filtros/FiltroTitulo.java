package modulos.agregacion.entities.filtros;

import lombok.Getter;
import lombok.Setter;
import modulos.shared.Hecho;
import modulos.buscadores.Normalizador;

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
        List<String> palabrasHecho = Normalizador.normalizarSeparado(hecho.getAtributosHecho().getTitulo());
        List<String> palabrasFiltro = Normalizador.normalizarSeparado(this.titulo);

        // Si la descripcion del hecho enviado por parametro tiene todas sus palabras contenidas en el filtro de la descripcion
        return palabrasHecho.containsAll(palabrasFiltro);
    }
}