package models.entities;

import models.entities.personas.*;

import lombok.Getter;

@Getter
public class SolicitudHecho {
    private Usuario usuario;
    private Hecho hecho;
    private long id;

    public SolicitudHecho(Usuario usuario, Hecho hecho, long id) {
        this.usuario = usuario;
        this.hecho = hecho;
        this.id = id;
    }
}
