import lombok.Getter;
import lombok.Setter;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

public class FiltroTitulo implements Filtro{
    @Getter
    @Setter
    String titulo;

    public FiltroTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Boolean aprobarHecho(Hecho hecho){
        List<String> palabrasHecho = Arrays.stream(hecho.getTitulo().toLowerCase().split(" "))
                .map(String::trim)
                .toList();

        List<String> palabrasFiltro = Arrays.stream(this.titulo.toLowerCase().split(" "))
                .map(String::trim)
                .toList();


        // Si la descripcion del hecho enviado por parametro tiene todas sus palabras contenidas en el filtro de la descripcion
        return palabrasHecho.containsAll(palabrasFiltro);
    }
}