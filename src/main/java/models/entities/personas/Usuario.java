package models.entities.personas;

import lombok.Getter;
import lombok.Setter;


public class Usuario {

    @Setter
    private String contrasenia;

    @Getter
    private Long id;

    public Usuario(Long id) {
        this.id = id;
    }

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