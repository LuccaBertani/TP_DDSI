package modulos.shared.utils;

import modulos.agregacion.entities.DbMain.usuario.Rol;
import modulos.agregacion.entities.DbMain.usuario.Usuario;

import java.util.Objects;

public class GestorRoles {

public static Boolean VisualizadorAContribuyente(Usuario usuario){

    if (!Objects.isNull(usuario) && usuario.getRol().equals(Rol.VISUALIZADOR)) {

        usuario.setRol(Rol.CONTRIBUYENTE);
        return true;
    }

        return false;
}

public static Boolean ContribuyenteAVisualizador(Usuario usuario){

    if (usuario.getCantHechosSubidos() == 0) {
        usuario.setRol(Rol.VISUALIZADOR);

        return true;
    }

    return false;
}

}
