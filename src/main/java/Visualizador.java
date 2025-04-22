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
        Contribuyente contribuyente = new Contribuyente(); // El visualizador pasa a ser contribuyent3e
        /*if (!this.getDatosPersonales().getNombre().isEmpty())
            contribuyente.setDatosPersonales(this.getDatosPersonales());*/
    }

    public void navegarPorHechos(){
        //TODO
    }

    // Devuelve los hechos que cumplan con todos los filtros
    public Set<Hecho> aplicarFiltros(List<Filtro> filtros){
        return Globales.hechosTotales.stream().filter(
                hecho -> filtros.
                                stream().
                                allMatch(filtro->filtro.aprobarHecho(hecho))
                ).collect(Collectors.toSet());
    }
}
