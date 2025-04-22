import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
public abstract class Publicador {
    private DatosPersonalesPublicador datosPersonales; // Si se setearon, significa que el publicador inició sesión

    public void subirHechos(List<Hecho> hechos){
        hechos.forEach(hecho -> hecho.setOrigen(Origen.CONTRIBUYENTE));
        Globales.hechosTotales.addAll(hechos);
    }

    public void navegarPorHechos(List<Filtro> filtros){
        Set<Hecho> lista = aplicarFiltros(filtros);
        for (Hecho hecho : lista){
            System.out.println(hecho.getTitulo());
        }
    }
    // Devuelve los hechos que cumplan con todos los filtros
    private Set<Hecho> aplicarFiltros(List<Filtro> filtros){
        return Globales.hechosTotales.stream().filter(
                hecho -> filtros.
                                stream().
                                allMatch(filtro->filtro.aprobarHecho(hecho))
                ).collect(Collectors.toSet());
    }
}
