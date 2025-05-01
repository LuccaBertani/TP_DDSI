package models.entities;

import models.entities.personas.*;

import lombok.Getter;

@Getter
public class SolicitudHecho {
    private Persona persona;
    private Hecho hecho;

    public SolicitudHecho(Persona persona, Hecho hecho) {
        this.persona = persona;
        this.hecho = hecho;
    }
}
