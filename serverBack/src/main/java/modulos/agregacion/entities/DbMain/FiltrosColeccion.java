package modulos.agregacion.entities.DbMain;

import lombok.Data;
import modulos.agregacion.entities.DbMain.filtros.*;


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
