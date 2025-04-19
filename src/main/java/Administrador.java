public class Administrador {

    public Coleccion crearColeccion(DatosColeccion datosColeccion){
        Coleccion coleccion = new Coleccion(datosColeccion);
        

        return coleccion;
    }
    public void importarHecho(Fuente fuente){
        //TODO
    }
    public Boolean evaluarSolicitudEliminacion(Hecho hecho){
        //TODO
        return true;
    }

    // Va a servir para proximas entregas
    public Boolean evaluarSolicitudHecho(Hecho hecho){
        //TODO
    }
}
