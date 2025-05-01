package models.entities.personas;

import lombok.Getter;

public class Persona {
    @Getter
    private Integer cantHechosSubidos = 0;

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

