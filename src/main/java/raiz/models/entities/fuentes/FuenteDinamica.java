package raiz.models.entities.fuentes;

import raiz.models.entities.*;

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
        hecho.setOrigen(Origen.FUENTE_DINAMICA);
        return hecho;
    }
}
