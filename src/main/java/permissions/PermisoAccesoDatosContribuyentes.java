package permissions;

import models.entities.personas.Persona;

public class PermisoAccesoDatosContribuyentes {
    private static Integer nivelRequerido = 2;

    public static Boolean tienePermisos(Persona persona){
        return persona.getNivel() >= PermisoAccesoDatosContribuyentes.nivelRequerido;
    }
}
