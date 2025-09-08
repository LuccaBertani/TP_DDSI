package modulos.agregacion.entities;

import lombok.Data;
import modulos.agregacion.entities.filtros.*;


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
    FiltroProvincia filtroProvincia;

}
