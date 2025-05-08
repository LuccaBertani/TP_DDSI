package models.entities;

import lombok.Getter;
import lombok.Setter;
import models.entities.filtros.Filtro;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
public class Visualizador {
    private DatosPersonalesPublicador datosPersonales; // Si se setearon, significa que el publicador inició sesión

    public void solicitudSubirHecho(Hecho hecho, ContextoPersona GestorPersona){
        Globales.solicitudesSubirHecho.add(new SolicitudHecho(GestorPersona, hecho));
    }


