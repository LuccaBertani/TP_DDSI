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

    @Override
    public Boolean aprobarHecho(Hecho hecho){
        List<String> palabrasHecho = Arrays.stream(hecho.getDescripcion().toLowerCase().split(" "))
                .map(String::trim)
                .toList(); // Mapear las palabras de la descripcion del hecho sin espacios y en minúsculas

        List<String> palabrasFiltro = Arrays.stream(this.descripcion.toLowerCase().split(" "))
                .map(String::trim)
                .toList();


        // Si la descripcion del hecho enviado por parametro tiene todas sus palabras contenidas en el filtro de la descripcion
        return palabrasHecho.containsAll(palabrasFiltro);
    }
}
