import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Visualizador extends Publicador{
    //private List<Hecho> hechosDisponibles;
    private List<Filtro> filtros;

    @Override
    public void subirHechos(List<Hecho> hechos){
        super.subirHechos(hechos);
        Contribuyente contribuyente = new Contribuyente(); // El visualizador pasa a ser contribuyente
        /*if (!this.getDatosPersonales().getNombre().isEmpty())
            contribuyente.setDatosPersonales(this.getDatosPersonales());*/
    }

}
