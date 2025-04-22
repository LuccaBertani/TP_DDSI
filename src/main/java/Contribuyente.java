import lombok.Getter;

@Getter
public class Contribuyente extends Visualizador {
    private Integer cantHechosSubidos; // Cuando llegue a 0, el contribuyente debería pasar a ser visualizador
    public Contribuyente(){
        cantHechosSubidos = 1;
        // Si el usuario se logueó y no está en la lista de contribuyentes de los administradores
        /*if (!this.getDatosPersonales().getNombre().isEmpty() && !Administrador.listaContribuyentesContains(this))
            Administrador.agregarContribuyente(this);*/
    }

    public void incrementarHechosSubidos(){
        this.cantHechosSubidos++;
    }

    public void disminuirHechosSubidos(){
        this.cantHechosSubidos--;
    }


    public void solicitarEliminacionHecho(Hecho hecho){
        ContextoPersona contextoPersona = new ContextoPersona((Visualizador)this);
        SolicitudHecho solicitud = new SolicitudHecho(contextoPersona, hecho);
        Globales.solicitudesEliminacion.add(solicitud);
    }


}
