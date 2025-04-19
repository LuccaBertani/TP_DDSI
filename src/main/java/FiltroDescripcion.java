import lombok.Getter;
import lombok.Setter;
import java.util.*;


public class FiltroDescripcion implements Filtro{
    @Getter
    @Setter
    String descripcion;

    public FiltroDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean aprobarHecho(Hecho hecho){
        List<String> descripcionEnlistada = Arrays.asList(hecho.getDescripcion().split(" "));
        List<String> descripcionFiltro = Arrays.asList(this.descripcion.toLowerCase().split(" "));
        descripcionEnlistada.replaceAll(String::toLowerCase);
        descripcionFiltro.replaceAll(String::toLowerCase);

        // Si la descripcion del hecho enviado por parametro tiene todas sus palabras contenidas en el filtro de la descripcion
        return descripcionFiltro.containsAll(descripcionEnlistada);
    }
}
