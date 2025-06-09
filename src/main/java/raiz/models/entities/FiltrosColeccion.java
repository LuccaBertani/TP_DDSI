package raiz.models.entities;

import lombok.Data;
import raiz.models.entities.filtros.*;

@Data
public class FiltrosColeccion {

    FiltroCategoria filtroCategoria;
    FiltroPais filtroPais;
    FiltroDescripcion filtroDescripcion;
    FiltroContenidoMultimedia filtroContenidoMultimedia;
    FiltroFechaAcontecimiento filtroFechaAcontecimiento;
    FiltroFechaCarga filtroFechaCarga;
    FiltroOrigen filtroOrigen;
    FiltroTitulo filtroTitulo;

}
