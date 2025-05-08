package models.entities;

import models.entities.personas.*;

import lombok.Getter;

@Getter
public class SolicitudHecho {
    private Usuario usuario;
    private Hecho hecho;

    public SolicitudHecho(Usuario usuario, Hecho hecho) {
        this.usuario = usuario;
        this.hecho = hecho;
    }
}
