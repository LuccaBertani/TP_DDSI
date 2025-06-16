package modulos.usuario;

import java.util.Objects;

public class GestorRoles {

public Boolean VisualizadorAContribuyente(Usuario usuario){

    if (!Objects.isNull(usuario) && usuario.getRol().equals(Rol.VISUALIZADOR)) {

        // Cambio el "rol" a contribuyente
        usuario.setRol(Rol.CONTRIBUYENTE);

        return true;
    }

        return false;
}

public Boolean ContribuyenteAVisualizador(Usuario usuario){

    if (usuario.getCantHechosSubidos() == 0) {

        // CAMBIAR ROL A VISUALIZADOR
        usuario.setRol(Rol.VISUALIZADOR);

        return true;
    }

    return false;
}

}
