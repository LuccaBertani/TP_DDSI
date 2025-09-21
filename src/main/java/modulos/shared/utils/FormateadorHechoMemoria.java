package modulos.shared.utils;

import modulos.AtributosHechoMemoria;
import modulos.agregacion.entities.AtributosHechoModificarMemoria;
import modulos.agregacion.entities.DbEstatica.HechoEstatica;
import modulos.agregacion.entities.DbMain.Hecho;
import modulos.agregacion.entities.HechoMemoria;
import modulos.agregacion.entities.atributosHecho.AtributosHechoModificar;
import modulos.buscadores.BuscadoresRegistry;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;


//AURA
@Component
public class FormateadorHechoMemoria {
    private final BuscadoresRegistry buscadores;
    public FormateadorHechoMemoria(BuscadoresRegistry buscadores) {
        this.buscadores = buscadores;
    }
    public HechoMemoria obtenerHechoMemoria(Hecho hecho){

        AtributosHechoMemoria atributos = AtributosHechoMemoria.builder().
                categoria(buscadores.getBuscadorCategoria().buscar(hecho.getAtributosHecho().getCategoria_id()))
                .descripcion(hecho.getAtributosHecho().getDescripcion())
                .latitud(hecho.getAtributosHecho().getLatitud()).longitud(hecho.getAtributosHecho().getLongitud())
                .modificado(hecho.getAtributosHecho().getModificado())
                .fechaCarga(hecho.getAtributosHecho().getFechaCarga())
                .origen(hecho.getAtributosHecho().getOrigen())
                .fechaAcontecimiento(hecho.getAtributosHecho().getFechaAcontecimiento())
                .fechaUltimaActualizacion(hecho.getAtributosHecho().getFechaUltimaActualizacion())
                .contenidoMultimedia(hecho.getAtributosHecho().getContenidoMultimedia())
                .titulo(hecho.getAtributosHecho().getTitulo())
                .ubicacion(buscadores.getBuscadorUbicacion().buscarUbicacion(hecho.getAtributosHecho().getUbicacion_id()))
                .build();

        List<AtributosHechoModificarMemoria> listaAtributosHechoModificar = new ArrayList<>();

        for (AtributosHechoModificar atributosModificar : hecho.getAtributosHechoAModificar()){
                AtributosHechoModificarMemoria atributos123 = AtributosHechoModificarMemoria.builder().
                        id(atributosModificar.getId()).
                        latitud(atributosModificar.getLatitud()).longitud(atributosModificar.getLongitud())
                        .categoria(buscadores.getBuscadorCategoria().buscar(atributosModificar.getCategoria()))
                        .titulo(atributosModificar.getTitulo())
                        .contenidoMultimedia(atributosModificar.getContenidoMultimedia()).descripcion(atributosModificar.getDescripcion())
                        .fechaAcontecimiento(atributosModificar.getFechaAcontecimiento())
                        .ubicacion(buscadores.getBuscadorUbicacion().buscarUbicacion(atributosModificar.getUbicacion_id()))
                        .build();

            listaAtributosHechoModificar.add(atributos123);
        }

        HechoMemoria hecho123 = HechoMemoria.builder().
                id(hecho.getId()).
                activo(hecho.getActivo()).
                usuario_id(hecho.getUsuario_id()).
                atributosHecho(atributos).
                atributosHechoAModificar(listaAtributosHechoModificar).
                build();

        if(hecho instanceof HechoEstatica hechoEstatica){
            hecho123.setDatasets(hechoEstatica.getDatasets());
        }

    return hecho123;
    }

}
