package services.impl;

import models.entities.Globales;
import models.entities.Hecho;
import models.entities.fuentes.Fuente;
import models.entities.personas.Contribuyente;
import models.entities.personas.Persona;
import models.entities.SolicitudHecho;
import models.repositories.IHechosRepository;
import models.repositories.ISolicitudHechoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import services.IHechosService;
import java.util.List;

@Service
public class HechosService implements IHechosService {

    @Autowired
    private IHechosRepository hechosRepository;

    @Autowired
    private ISolicitudHechoRepository solicitudHechoRepository;


    @Override
    public void subirHecho(Hecho hecho, Persona persona) {
        // Verificar
        hechosRepository.save(hecho);
    }

    @Override
    public void solicitarSubirHecho(Hecho hecho, Persona persona) {
        // LA PERSONA DEBE SER O VISUALIZADORA O CONTRIBUYENTE
        SolicitudHecho solicitudHecho = new SolicitudHecho(persona, hecho);
        solicitudHechoRepository.save(solicitudHecho);
    }

    @Override
    public void evaluarSolicitudSubirHecho(SolicitudHecho solicitud, Boolean respuesta) {
        if(respuesta){
            // TODO
            if(solicitud.getGestorPersona().getVisualizador().getClass().getSimpleName().equals("Visualizador")
                    && !solicitud.getGestorPersona().getVisualizador().getDatosPersonales().getNombre().isEmpty()){
                solicitud.getGestorPersona().VisualizadorAContribuyente();//cambio de estado
                listaContribuyentes.add((Contribuyente) solicitud.getGestorPersona().getVisualizador());
            }
            else{
                Contribuyente contribuyente = (Contribuyente) solicitud.getGestorPersona().getVisualizador();
                contribuyente.incrementarHechosSubidos();

            }
        }
        solicitudHechoRepository.delete(solicitud);
    }

    @Override
    public void evaluarEliminacionHecho(SolicitudHecho solicitud, Boolean respuesta) {
        if(respuesta){
            solicitudHechoRepository.delete(solicitud);
            // El hecho deberia dejar de mostrarse en la pagina, pero NUNCA se borra por completo
            solicitud.getPersona().disminuirHechosSubidos();
            if (solicitud.getPersona().getCantHechosSubidos() == 0){
                // CAMBIAR ROL A VISUALIZADOR

            }

        }

    }

    public void importarHechos(Fuente fuente){
        List<Hecho> hechos = fuente.leerFuente();

        for (Hecho hecho : hechos){
            hechosRepository.save(hecho);
        }

    }
}
