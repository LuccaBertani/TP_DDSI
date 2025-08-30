package modulos.agregacion.entities.filtros;

import lombok.Getter;
import lombok.Setter;
import modulos.shared.Hecho;
import modulos.buscadores.Normalizador;

import java.util.List;


public class FiltroDescripcion implements Filtro {
    @Getter
    @Setter
    String descripcion;

    public FiltroDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public Boolean aprobarHecho(Hecho hecho){
        List<String> palabrasHecho = Normalizador.normalizarSeparado(hecho.getAtributosHecho().getDescripcion());
        List<String> palabrasFiltro = Normalizador.normalizarSeparado(this.descripcion);

        // Si la descripcion del hecho enviado por parametro tiene todas sus palabras contenidas en el filtro de la descripcion
        return palabrasHecho.containsAll(palabrasFiltro);
    }
}
