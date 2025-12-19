package modulos.servicioEstadistica;

import modulos.agregacion.entities.DbMain.*;
import modulos.agregacion.entities.fuentes.LectorCSV;
import modulos.apis.IDatosQuery;
import modulos.servicioEstadistica.entities.*;
import modulos.servicioEstadistica.repositories.IEstadisticasRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ServicioDeEstadistica {

    private final IDatosQuery datosQuery;
    private final IEstadisticasRepository estadisticasRepository;

    private Estadisticas estadisticasActuales;

    public ServicioDeEstadistica(IDatosQuery datosQuery, IEstadisticasRepository estadisticasRepository) {
        this.datosQuery = datosQuery;
        this.estadisticasRepository = estadisticasRepository;
    }

    @Async
    @Scheduled(cron = "${miapp.cron}")//tiempo en properties
    public void generarEstadistica(){

        List<ColeccionProvincia> coleccionProvincias = datosQuery.obtenerMayorCantHechosProvinciaEnColeccion();

        CategoriaCantidad categoriaCantidad = datosQuery.categoriaMayorCantHechos();

        List<CategoriaProvincia> categoriaProvincias = datosQuery.mayorCantHechosCategoriaXProvincia();

        List<CategoriaHora> categoriaHoras = datosQuery.horaMayorCantHechos();

        Long cantidadDeSpam = datosQuery.cantSolicitudesEliminacionSpam();
        Estadisticas estadisticas = new Estadisticas(cantidadDeSpam,
            categoriaCantidad, categoriaHoras, categoriaProvincias, coleccionProvincias);

        this.estadisticasActuales = estadisticas;
        this.estadisticasRepository.save(estadisticas);
    }


    public Path exportarEstadisticaSpam() {
        if (this.estadisticasActuales == null || this.estadisticasActuales.getCantidadDeSpam() == null) {
            return null;
        }

        List<Object> valores;
        List<String> header;
        String nombre = "estadisticaSpam.csv";
        header = List.of("totalSpam");
        Long total = this.estadisticasActuales.getCantidadDeSpam();

        valores = List.of(total);
        return LectorCSV.generarCsvDesdeListaLineal(valores, nombre, header);
    }

    public Path exportarEstadisticaCategoriaMayorCantidadHechos() {
        if (this.estadisticasActuales == null || this.estadisticasActuales.getCategoriaCantidad() == null) {
            return null;
        }
        List<Object> valores = new ArrayList<>();
        List<String> header;
        String nombre = "estadisticaCategoriaMasHechos.csv";
        header = List.of("categoria_id", "categoria_titulo", "cantidad");

            var cc = this.estadisticasActuales.getCategoriaCantidad();
            Long categoria_id = Optional.ofNullable(cc)
                    .map(CategoriaCantidad::getCategoria_id)
                    .orElse(null);
            Categoria categoria = datosQuery.findCategoriaById(categoria_id);
            String categoriaStr = categoria != null ? categoria.getTitulo() : null;

            Integer cantidad = Optional.ofNullable(cc)
                    .map(CategoriaCantidad::getCantidad)
                    .orElse(0);

            valores.add(categoria_id);
            valores.add(categoriaStr);
            valores.add(cantidad);
            return LectorCSV.generarCsvDesdeListaLineal(valores, nombre, header);


    }

    public Path exportarEstadisticaHoraMayorCantidadHechosCategorias(){
        if (this.estadisticasActuales == null || this.estadisticasActuales.getCategoriaHoras() == null) {
            return null;
        }
        List<Object> valores = new ArrayList<>();
        List<String> header;

        String nombre = "estadisticaHoraMasHechosXCategoria.csv";
        header = List.of("categoria_id", "categoria_titulo", "hora", "cantidad");

        List<CategoriaHora> lista = Optional.ofNullable(this.estadisticasActuales.getCategoriaHoras())
                .orElse(List.of());

            for (var v : lista) {
                if (v == null) continue;
                Long categoria_id = v.getCategoria_id();
                Categoria categoria = datosQuery.findCategoriaById(categoria_id);
                String categoriaStr = categoria != null ? categoria.getTitulo() : null;

                Integer hora = v.getHora();
                Integer cant = Optional.ofNullable(v.getCantidad()).orElse(0);

                valores.add(categoria_id);
                valores.add(categoriaStr);
                valores.add(hora);
                valores.add(cant);
            }

            return LectorCSV.generarCsvDesdeListaLineal(valores, nombre, header);
    }


    public Path exportarEstadisticaMayorCantidadHechosCategoriasProvincias(){
        if (this.estadisticasActuales == null || this.estadisticasActuales.getCategoriaProvincias() == null) {
            return null;
        }
        List<Object> valores = new ArrayList<>();
        List<String> header;

        String nombre = "estadisticaProvinciaMasHechosXCategoria.csv";
        header = List.of("categoria_id", "categoria_titulo", "provincia_id", "provincia_nombre", "cantidad");

        List<CategoriaProvincia> lista = Optional.ofNullable(this.estadisticasActuales.getCategoriaProvincias())
                .orElse(List.of());

            for (var v : lista) {
                if (v == null) continue;
                Long categoria_id = v.getCategoria_id();
                Categoria categoria = datosQuery.findCategoriaById(categoria_id);
                String categoriaStr = categoria != null ? categoria.getTitulo() : null;
                Long provincia_id = v.getProvincia_id();
                Provincia provincia = datosQuery.findProvinciaById(provincia_id);
                String provinciaStr = provincia!=null ? provincia.getProvincia() : null;
                Long cant = v.getCantidad();

                valores.add(categoria_id);
                valores.add(categoriaStr);
                valores.add(provincia_id);
                valores.add(provinciaStr);
                valores.add(cant);
            }

            return LectorCSV.generarCsvDesdeListaLineal(valores, nombre, header);
    }

    public Path exportarEstadisticaProvinciaMayorCantidadHechosColecciones(){
        if (this.estadisticasActuales == null || this.estadisticasActuales.getColeccionProvincias() == null) {
            return null;
        }
        List<Object> valores = new ArrayList<>();
        List<String> header;
        String nombre = "estadisticaProvinciaMasHechosXCategoria.csv";
        header = List.of("coleccion_id", "coleccion_titulo", "provincia_id", "provincia_nombre", "cantidad");

        List<ColeccionProvincia> lista = Optional.ofNullable(this.estadisticasActuales.getColeccionProvincias())
                .orElse(List.of());

            for (var v : lista) {
                if (v == null) continue;
                Long coleccion_id = v.getColeccion_id();
                Coleccion coleccion = datosQuery.findColeccionById(coleccion_id);
                String coleccionStr = coleccion!=null?coleccion.getTitulo():null;
                Long provincia_id = v.getProvincia_id();
                Provincia provincia = datosQuery.findProvinciaById(provincia_id);
                String provinciaStr = provincia!=null ? provincia.getProvincia() : null;
                Long cant = v.getCantidad();

                valores.add(coleccion_id);
                valores.add(coleccionStr);
                valores.add(provincia_id);
                valores.add(provinciaStr);
                valores.add(cant);
            }

            return LectorCSV.generarCsvDesdeListaLineal(valores, nombre, header);
    }
}


