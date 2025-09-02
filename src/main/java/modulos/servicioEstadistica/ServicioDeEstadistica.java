package modulos.servicioEstadistica;

import modulos.agregacion.entities.*;
import modulos.agregacion.entities.fuentes.LectorCSV;
import modulos.apis.IDatosQuery;
import modulos.servicioEstadistica.Entidad.Estadisticas;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;

@Service
public class ServicioDeEstadistica {

    IDatosQuery datosQuery;

    Estadisticas estadisticas;


    public ServicioDeEstadistica(IDatosQuery datosQuery) {
        this.datosQuery = datosQuery;
    }

    @Async
    @Scheduled(cron = "${miapp.cron}")//tiempo en properties
    public void generarEstadistica(){
    Integer cantidadDeSpam = datosQuery.cantSolicitudesEliminacionSpam();
    CategoriaCantidad categoriaCantidad = datosQuery.mayorCantHechosCategoria();
    List<CategoriaHora> categoriaHoras = datosQuery.horaMayorCantHechos();
    List<CategoriaProvincia> categoriaProvincias = datosQuery.obtenerMayorCantHechosProvincia();
    List<ColeccionProvincia> coleccionProvincias = datosQuery.obtenerMayorCantHechosProvinciaEnColeccion();
    estadisticas = new Estadisticas(cantidadDeSpam,
            categoriaCantidad, categoriaHoras, categoriaProvincias, coleccionProvincias);
    // generar csv ashei

    }

//id 1 : spam id 2 : categoria con mayor cant de hechos id 3: hora de mayor cantidad de hechos id 4: provincia con mayor cantidad de hechos de una categoria
//id 5 : provincia con mayor cantidad de hechos de una coleccion
    public Path obtenerEstadistica(int id){
        switch(id){
            case 1:{
                String nombre = "estadisticaSpam.csv";
                return LectorCSV.generarCsvDesdeObjeto(estadisticas.getCantidadDeSpam(), nombre);
            }
            case 2:{
                String nombre = "estadisticaCategoriaMasHechos.csv";
                return LectorCSV.generarCsvDesdeObjeto(estadisticas.getCategoriaCantidad(), nombre);
            }
            case 3:{
                String nombre = "estadisticaHoraMasHechosXCategoria.csv";
                return LectorCSV.generarCsvDesdeObjeto(estadisticas.getCategoriaHoras(), nombre);
            }
            case 4:{
                String nombre = "estadisticaProvinciaMasHechosXCategoria.csv";
                return LectorCSV.generarCsvDesdeObjeto(estadisticas.getCategoriaProvincias(), nombre);
            }
            case 5:{
                String nombre = "estadisticaProvinciaMasHechosXColeccion.csv";
                return LectorCSV.generarCsvDesdeObjeto(estadisticas.getColeccionProvincias(), nombre);
            }
            default:
                return null;
        }
    }





}

/*
* SERVICIOA -> tiene inyectado a la interfaz X -> la interfaz X tiene una implementacion con las funcionalidades que necesita A -> se conecta a B
*
* */


/*@GetMapping(value = "/reporte.csv", produces = "text/csv")
public ResponseEntity<String> descargarCsv() {
    StringBuilder csv = new StringBuilder();
    // (opcional) BOM para que Excel lea UTF-8
    csv.append('\uFEFF');
    csv.append("id,nombre\n");
    csv.append("1,Ana\n");
    csv.append("2,Juan\n");

    return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=\"reporte.csv\"")
            .body(csv.toString());
}
*/