package modulos.shared.utils;

import modulos.agregacion.entities.AtributosHechoModificarMemoria;
import modulos.agregacion.entities.DbDinamica.HechoDinamica;
import modulos.agregacion.entities.DbEstatica.HechoEstatica;
import modulos.agregacion.entities.DbMain.Hecho;
import modulos.agregacion.entities.DbProxy.HechoProxy;
import modulos.agregacion.entities.HechoMemoria;
import modulos.agregacion.entities.atributosHecho.AtributosHecho;
import modulos.agregacion.entities.atributosHecho.AtributosHechoModificar;
import modulos.buscadores.BuscadoresRegistry;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class FormateadorHechoBDD {

    public FormateadorHechoBDD(BuscadoresRegistry buscadores){
        this.buscadores = buscadores;
    }

    public <T extends Hecho> T formatearHechoBDD(HechoMemoria hecho, Class<T> tipo){
        AtributosHecho atributos = AtributosHecho.builder()
                .categoria_id(hecho.getAtributosHecho().getCategoria().getId())
                .descripcion(hecho.getAtributosHecho().getDescripcion())
                .latitud(hecho.getAtributosHecho().getLatitud())
                .longitud(hecho.getAtributosHecho().getLongitud())
                .modificado(hecho.getAtributosHecho().getModificado())
                .fechaCarga(hecho.getAtributosHecho().getFechaCarga())
                .origen(hecho.getAtributosHecho().getOrigen())
                .fechaAcontecimiento(hecho.getAtributosHecho().getFechaAcontecimiento())
                .fechaUltimaActualizacion(hecho.getAtributosHecho().getFechaUltimaActualizacion())
                .contenidoMultimedia(hecho.getAtributosHecho().getContenidoMultimedia())
                .titulo(hecho.getAtributosHecho().getTitulo())
                .ubicacion_id(hecho.getAtributosHecho().getUbicacion().getId())
                .build();

        List<AtributosHechoModificar> listaAtributosHechoModificar = new ArrayList<>();

        for (AtributosHechoModificarMemoria atributosModificar : hecho.getAtributosHechoAModificar()){
            AtributosHechoModificar atributos123 = AtributosHechoModificar.builder()
                    .id(atributosModificar.getId())
                    .latitud(atributosModificar.getLatitud())
                    .longitud(atributosModificar.getLongitud())
                    .categoria(atributosModificar.getCategoria().getId())
                    .titulo(atributosModificar.getTitulo())
                    .contenidoMultimedia(atributosModificar.getContenidoMultimedia())
                    .fechaAcontecimiento(atributosModificar.getFechaAcontecimiento())
                    .ubicacion_id(atributosModificar.getUbicacion().getId())
                    .build();
            listaAtributosHechoModificar.add(atributos123);
        }

        Hecho hecho123 = null;

        switch (hecho.getAtributosHecho().getOrigen()){
            case CARGA_MANUAL, FUENTE_DINAMICA: {
                hecho123 = HechoDinamica.builder()
                        .id(hecho.getId())
                        .activo(hecho.getActivo())
                        .usuario_id(hecho.getUsuario_id())
                        .atributosHecho(atributos)
                        .atributosHechoAModificar(listaAtributosHechoModificar)
                        .build();
                break;
            }
            case FUENTE_ESTATICA: {
                hecho123 = HechoEstatica.builder()
                        .id(hecho.getId())
                        .activo(hecho.getActivo())
                        .usuario_id(hecho.getUsuario_id())
                        .datasets(hecho.getDatasets())
                        .atributosHecho(atributos)
                        .atributosHechoAModificar(listaAtributosHechoModificar)
                        .build();
                break;
            }
            case FUENTE_PROXY_METAMAPA:{
                hecho123 = HechoProxy.builder()
                        .id(hecho.getId())
                        .activo(hecho.getActivo())
                        .usuario_id(hecho.getUsuario_id())
                        .atributosHecho(atributos)
                        .atributosHechoAModificar(listaAtributosHechoModificar)
                        .build();
                break;
            }
            default:
                System.out.println("Nunca voy a entrar acá (?)");
                return null;
        }

        return tipo.cast(hecho123);

    }
}
