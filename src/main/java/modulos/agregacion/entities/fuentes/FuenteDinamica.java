package modulos.agregacion.entities.fuentes;

import modulos.agregacion.entities.DbDinamica.HechoDinamica;
import modulos.agregacion.entities.DbMain.HechosData;
import modulos.agregacion.entities.atributosHecho.Origen;
import modulos.agregacion.entities.atributosHecho.TipoContenido;
import modulos.shared.utils.FechaParser;

import java.time.ZonedDateTime;


public class FuenteDinamica {

    public HechoDinamica crearHecho(HechosData data){

        HechoDinamica hecho = new HechoDinamica();
        hecho.getAtributosHecho().setTitulo(data.getTitulo());
        hecho.setActivo(false);
        hecho.getAtributosHecho().setDescripcion(data.getDescripcion());
        TipoContenido contenido = data.getTipoContenido() != null ? TipoContenido.fromCodigo(data.getTipoContenido()) : null;
        hecho.getAtributosHecho().setContenidoMultimedia(contenido);
        ZonedDateTime fecha = FechaParser.parsearFecha(data.getFechaAcontecimiento());
        hecho.getAtributosHecho().setFechaAcontecimiento(fecha);
        hecho.getAtributosHecho().setUbicacion(data.getUbicacion());
        hecho.getAtributosHecho().setOrigen(Origen.FUENTE_DINAMICA);
        hecho.getAtributosHecho().setModificado(true);
        hecho.getAtributosHecho().setCategoria(data.getCategoria());
        hecho.getAtributosHecho().setLatitud(data.getLatitud());
        hecho.getAtributosHecho().setLatitud(data.getLongitud());
        return hecho;
    }
}

//traes un hecho de estatica -> modificado en true -> pasan 3 horas (se activa el cronjob)
//-> evalua los hechos que tienen el bool en true -> lo mete en la coleccion que corresponda -> pone el boolean en false


/*
Se crea hecho incendio con modificado = true
Pasa una hora y se mapea a coleccion de incendios
modificado = false
Se crea una nueva coleccion llamada tragedias
pasa una hora


TABLA DE HECHOS [1,2,3,4,5,6]

ITERA EN TODA LA TABLA Y AGARRA LOS QUE TENGAN EL BOOL MODIFICADO EN TRUE ->

hechos no modificados [1,2,3,4]
hechos modificados [5,6]
pasa una hora
lee la tabla de hechos -> y agarra a [5,6]
se fija en todas las colecciones si cumple en alguna y los mete
pone en false [5,6]
hechos no modificados [1,2,3,4,5,6]
se crea una colección con atributo modificado en true
colecciones no modificadas [1,2,3,4]
colecciones modificadas [5,6]
pasa una hora
lee la tabla de colecciones -> y agarra [5,6]
con la coleccion 5 -> checkea todos los hechos del sistema y saca y mete los que cumplan y no
y lo mismo con la 6ta
cuando termina pone en false la [5,6]

RESUMEN: HECHOS MODIFICADOS o CREADOS: SOLO SE VEN LOS HECHOS MODIFICADOS
        COLECCION NUEVA O MODIFICADA: SE BARREN TODOS LOS HECHOS
*/

