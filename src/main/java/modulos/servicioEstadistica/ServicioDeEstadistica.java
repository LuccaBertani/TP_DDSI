package modulos.servicioEstadistica;

import modulos.agregacion.entities.*;
import modulos.agregacion.entities.fuentes.LectorCSV;
import modulos.apis.IDatosQuery;
import modulos.servicioEstadistica.Entidad.Estadisticas;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
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
        CantSolicitudesEliminacionSpam cantidadDeSpam = datosQuery.cantSolicitudesEliminacionSpam();
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

        List<Object> valores = new ArrayList<>();
        List<String> header = new ArrayList<>();

        switch(id){
            case 1:{
                String nombre = "estadisticaSpam.csv";
                valores = Arrays.asList(
                        estadisticas.getCantidadDeSpam()
                );

                Field[] campos = CantSolicitudesEliminacionSpam.class.getDeclaredFields();

                header.add(campos[0].getName());

                return LectorCSV.generarCsvDesdeListaLineal(valores, nombre, header);
            }
            case 2: {
                String nombre = "estadisticaCategoriaMasHechos.csv";


                valores = Arrays.asList(
                        estadisticas.getCategoriaCantidad().getCategoria().getTitulo(),
                        estadisticas.getCategoriaCantidad().getCantidad()
                );

                Field[] campos = CategoriaCantidad.class.getDeclaredFields();
                for (int i = 1; i < campos.length; i++) {
                    header.add(campos[i].getName());
                }

                return LectorCSV.generarCsvDesdeListaLineal(valores, nombre, header);
            }
            case 3:{
                String nombre = "estadisticaHoraMasHechosXCategoria.csv";

                valores = new ArrayList<>();

                for(CategoriaHora valor : estadisticas.getCategoriaHoras()){
                    valores.add(valor.getCategoria().getTitulo());
                    valores.add(valor.getHora());
                    valores.add(valor.getCantidad());
                }

                Field[] campos = CategoriaHora.class.getDeclaredFields();
                for (int i = 1; i < campos.length; i++) {
                    header.add(campos[i].getName());
                }

                return LectorCSV.generarCsvDesdeListaLineal(valores, nombre, header);
            }
            case 4:{
                String nombre = "estadisticaProvinciaMasHechosXCategoria.csv";

                for (CategoriaProvincia valor: estadisticas.getCategoriaProvincias()){
                    valores.add(valor.getCategoria().getTitulo());
                    valores.add(valor.getProvincia().getProvincia());
                    valores.add(valor.getCantidad());
                }

                Field[] campos = CategoriaProvincia.class.getDeclaredFields();
                for (int i = 1; i < campos.length; i++) {
                    header.add(campos[i].getName());
                }

                return LectorCSV.generarCsvDesdeListaLineal(valores, nombre, header);
            }
            case 5:{
                String nombre = "estadisticaProvinciaMasHechosXColeccion.csv";

                for(ColeccionProvincia valor : estadisticas.getColeccionProvincias()){
                    valores.add(valor.getColeccion().getTitulo());
                    valores.add(valor.getProvincia().getProvincia());
                    valores.add(valor.getCantidad());
                }

                Field[] campos = ColeccionProvincia.class.getDeclaredFields();
                for (int i = 1; i < campos.length; i++) {
                    header.add(campos[i].getName());
                }

                return LectorCSV.generarCsvDesdeListaLineal(valores, nombre, header);
            }
            default:
                return null;
        }
    }





}

/*
                Categoria catInc = new Categoria();
                catInc.setTitulo("Incendios");

                Categoria catRobo = new Categoria();
                catRobo.setTitulo("Robos");

                Provincia provBA = new Provincia();
                provBA.setProvincia("Buenos Aires");

                Provincia provCBA = new Provincia();
                provCBA.setProvincia("Córdoba");


                Estadisticas est1 = new Estadisticas();

// Filas (categoria, provincia, cantidad)
                CategoriaProvincia cp1 = new CategoriaProvincia(catInc,  provBA, 120);
                CategoriaProvincia cp2 = new CategoriaProvincia(catInc,  provCBA, 45);
                CategoriaProvincia cp3 = new CategoriaProvincia(catRobo, provCBA, 90);

                est1.getCategoriaProvincias().add(cp1);
                est1.getCategoriaProvincias().add(cp2);
                est1.getCategoriaProvincias().add(cp3);
                // ====== Armado de valores y header para el CSV ======
                List<Object> merca = new ArrayList<>();
                for (CategoriaProvincia valor : est1.getCategoriaProvincias()) {
                    merca.add(valor.getCategoria().getTitulo());     // String
                    merca.add(valor.getProvincia().getProvincia());  // String
                    merca.add(valor.getCantidad());                  // Integer
                }

                Estadisticas falopa = new Estadisticas();
                falopa.setCategoriaCantidad(new CategoriaCantidad());
                falopa.getCategoriaCantidad().setCategoria(new Categoria());
                falopa.getCategoriaCantidad().getCategoria().setTitulo("BOLIVINAS");
                falopa.getCategoriaCantidad().setCantidad(200000000);

                Estadisticas falopa2 = new Estadisticas();
                falopa2.setCategoriaCantidad(new CategoriaCantidad());
                falopa2.getCategoriaCantidad().setCategoria(new Categoria());
                falopa2.getCategoriaCantidad().getCategoria().setTitulo("ARGENTINAS");
                falopa2.getCategoriaCantidad().setCantidad(2);
*/


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