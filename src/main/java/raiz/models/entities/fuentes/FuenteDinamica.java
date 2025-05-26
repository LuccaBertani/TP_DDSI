package raiz.models.entities.fuentes;

import raiz.models.entities.FechaParser;
import raiz.models.entities.Hecho;
import raiz.models.entities.HechosData;
import raiz.models.entities.TipoContenido;

import java.time.ZonedDateTime;


public class FuenteDinamica {

    public Hecho crearHecho(HechosData data){

        Hecho hecho = new Hecho();
        hecho.setId(data.getId());
        hecho.setTitulo(data.getTitulo());
        hecho.setActivo(false);
        hecho.setDescripcion(data.getDescripcion());
        hecho.setContenidoMultimedia(TipoContenido.fromCodigo(data.getTipoContenido()));
        ZonedDateTime fecha = FechaParser.parsearFecha(data.getFechaAcontecimiento());
        hecho.setFechaAcontecimiento(fecha);
        hecho.setFechaDeCarga(ZonedDateTime.now());
        hecho.setPais(data.getPais());

        return hecho;
    }
}
