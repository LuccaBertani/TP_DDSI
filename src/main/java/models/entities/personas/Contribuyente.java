package models.entities.personas;

import lombok.Getter;
import models.entities.*;

/*
* Contribuyentes: personas que suben hechos a la plataforma, ya sean anónimas o registradas con datos personales.
* */

@Getter
public class Contribuyente extends Visualizador {

    Usuario usuario;

    public Contribuyente(Usuario usuario) {
        this.usuario = usuario;
    }

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


}
