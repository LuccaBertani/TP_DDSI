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
        List<String> descripcionEnlistada = Arrays.asList(hecho.getTitulo().split(" "));
        List<String> descripcionFiltro = Arrays.asList(this.titulo.toLowerCase().split(" "));
        descripcionEnlistada.replaceAll(String::toLowerCase);
        descripcionFiltro.replaceAll(String::toLowerCase);

        for (int i = 0; i < descripcionEnlistada.size(); i++){
            System.out.println("INDICE: "+ i);
            System.out.println(descripcionEnlistada.get(i));
        }

        // Si el titulo del hecho enviado por parametro tiene todas sus palabras contenidas en el filtro del titulo
        return descripcionFiltro.containsAll(descripcionEnlistada);
    }
}