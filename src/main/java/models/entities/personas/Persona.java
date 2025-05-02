package models.entities.personas;

import lombok.Getter;
import lombok.Setter;
import models.entities.DatosPersonalesPublicador;

public class Persona {


    @Getter
    private Integer cantHechosSubidos = 0;

    @Getter
    @Setter
    private Integer nivel = 0; // 0 visualizador, 1 contribuyente, 2 admin

    @Getter
    @Setter
    private DatosPersonalesPublicador datosPersonales;


    public void incrementarNivel(){
        this.nivel++;
    }

    public void disminuirNivel(){
        this.nivel--;
    }

    public void incrementarHechosSubidos(){
        this.cantHechosSubidos++;
    }

    public void disminuirHechosSubidos(){
        this.cantHechosSubidos--;
    }

    //private Rol rol;  COMO HACEMOS LOS ROLES?


}



/*public class Rol{
    public void mondongo();
}


public interface Rol {
    void ejecutarAccion(); // Método genérico que cada rol implementará a su manera
}

public class Administrador implements Rol {
    @Override
    public void ejecutarAccion() {
        System.out.println("Acción de administrador: gestionar el sistema.");
    }

    public void eliminarUsuario() {
        System.out.println("Usuario eliminado.");
    }
}

public class Visualizador implements Rol {
    @Override
    public void ejecutarAccion() {
        System.out.println("Acción de visualizador: ver información.");
    }
}

public class Contribuyente implements Rol {
    @Override
    public void ejecutarAccion() {
        System.out.println("Acción de contribuyente: subir contenido.");
    }
}

public class Persona {
    private String nombre;
    private Rol rol;

    public Persona(String nombre, Rol rol) {
        this.nombre = nombre;
        this.rol = rol;
    }

    public void realizarAccion() {
        rol.ejecutarAccion();
    }

    public void cambiarRol(Rol nuevoRol) {
        this.rol = nuevoRol;
    }

    public String getNombre() {
        return nombre;
    }

    public Rol getRol() {
        return rol;
    }
}*/

