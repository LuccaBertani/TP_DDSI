package models.entities.personas;

import lombok.Getter;
import lombok.Setter;
import models.entities.DatosPersonalesPublicador;
import models.entities.Visualizador;


public class Usuario {
    String contrasenia;

    @Getter
    private Integer cantHechosSubidos = 0;

    @Getter
    @Setter
    private Rol rol = Rol.VISUALIZADOR; // 0 visualizador, 1 contribuyente, 2 admin

    @Getter
    @Setter
    private DatosPersonalesPublicador datosPersonales;



    public void incrementarHechosSubidos(){
        this.cantHechosSubidos++;
    }

    public void disminuirHechosSubidos(){
        this.cantHechosSubidos--;
    }



}