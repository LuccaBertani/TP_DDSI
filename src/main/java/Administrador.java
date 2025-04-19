import java.util.List;

public class Administrador {
    //Criterios es una lista traida supongo del front donde el usuario indica los filtros y la lista de hechos totales es una global
    public Coleccion crearColeccion(DatosColeccion datosColeccion, List<Filtro> criterios, List<Hecho> hechosTotales){
        Coleccion coleccion = new Coleccion(datosColeccion);
        coleccion.getCriterio().addAll(criterios);
        Globales.hechosTotales.forEach(hecho -> {
            Boolean condicion = coleccion.getCriterio().stream()
                    .allMatch(criterio -> criterio.aprobarHecho(hecho));
            if(condicion) {
            coleccion.getHechos().add(hecho);
            }
        });
        return coleccion;
    }
    //Esos hechos habrian que agregarlos a la lista global
    public void importarHecho(Fuente fuente){
        Globales.hechosTotales.addAll(fuente.leerFuente());
    }
    //Me imagino que este metodo se llama en el caso que el administrador acepte la solicitud de eliminacion
    public Boolean evaluarSolicitudEliminacion(Hecho hecho){
        //TODO
        return true;
    }

    // Va a servir para proximas entregas
    public Boolean evaluarSolicitudHecho(Hecho hecho){
        //TODO
        return true;
    }
}
