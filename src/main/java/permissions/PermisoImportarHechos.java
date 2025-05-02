package permissions;

import models.entities.personas.Persona;

public class PermisoImportarHechos{
    private static Integer nivelNecesario = 0;

    public static Boolean tienePermisos(Persona persona){
        return persona.getNivel() >= PermisoImportarHechos.nivelNecesario;
    }
}
