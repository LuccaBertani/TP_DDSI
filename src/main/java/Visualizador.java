import java.util.List;
import java.util.Objects;

public class Visualizador extends Publicador{
    private List<Hecho> hechosDisponibles;
    private List<Filtro> filtros;

    @Override
    public void subirHechos(List<Hecho> hechos){
        super.subirHechos(hechos);
        Contribuyente contribuyente = new Contribuyente(); // El visualizador pasa a ser contribuyent3e
        contribuyente.setDatosPersonales(this.getDatosPersonales());
    }

    public void navegarPorHechos(){
        //TODO
    }

    // Devuelve los hechos que cumplan con todos los filtros
    public List<Hecho> aplicarFiltros(List<Filtro> filtros){
        return hechosDisponibles.stream().filter(
                hecho -> filtros.
                                stream().
                                allMatch(filtro->filtro.aprobarHecho(hecho))
                ).toList();
    }
}
