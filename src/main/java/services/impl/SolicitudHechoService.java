package services.impl;

import models.entities.*;
import models.entities.personas.Persona;
import models.repositories.IMemoriaPersonaRepository;
import models.repositories.IMemoriaSolicitudAgregarHechoRepository;
import models.repositories.IMemoriaSolicitudEliminarHechoRepository;
import models.repositories.IRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import services.ISolicitudHechoService;

@Service
public class SolicitudHechoService implements ISolicitudHechoService {

    private final IMemoriaSolicitudAgregarHechoRepository solicitudAgregarHechoRepo;
    private final IMemoriaSolicitudEliminarHechoRepository solicitudEliminarHechoRepo;

    private final IMemoriaPersonaRepository personaRepository;

    @Autowired
    public SolicitudHechoService(IMemoriaSolicitudAgregarHechoRepository solicitudAgregarHechoRepo,IMemoriaSolicitudEliminarHechoRepository solicitudEliminarHechoRepo,
                                 IMemoriaPersonaRepository personaRepository) {
        this.solicitudAgregarHechoRepo = solicitudAgregarHechoRepo;
        this.solicitudEliminarHechoRepo = solicitudEliminarHechoRepo;
        this.personaRepository = personaRepository;
    }

    @Override
    public void solicitarSubirHecho(Hecho hecho, Persona persona) {
        // LA PERSONA DEBE SER O VISUALIZADORA O CONTRIBUYENTE
        SolicitudHecho solicitudHecho = new SolicitudHecho(persona, hecho);
        solicitudAgregarHechoRepo.save(solicitudHecho);
    }

    @Override
    public void solicitarEliminacionHecho(Persona persona, Hecho hecho){

        SolicitudHecho solicitud = new SolicitudHecho(persona, hecho);

        solicitudEliminarHechoRepo.save(solicitud);
    }

    @Override
    public void evaluarSolicitudSubirHecho(SolicitudHecho solicitud, Boolean respuesta) {
        if(respuesta){
            solicitud.getHecho().setActivo(true);
            if(solicitud.getPersona().getNivel() == 0
                    && !solicitud.getPersona().getDatosPersonales().getNombre().isEmpty()){

                // Cambio el "rol" a contribuyente
                solicitud.getPersona().incrementarNivel();

                this.personaRepository.save(solicitud.getPersona());

            }
            solicitud.getPersona().incrementarHechosSubidos();
        }
        this.solicitudAgregarHechoRepo.delete(solicitud);
    }

    @Override
    public void evaluarEliminacionHecho(SolicitudHecho solicitud, Boolean respuesta) {
        if(respuesta){
            solicitudEliminarHechoRepo.delete(solicitud);
            // El hecho deberia dejar de mostrarse en la pagina, pero NUNCA se borra por completo
            solicitud.getHecho().setActivo(false);

            solicitud.getPersona().disminuirHechosSubidos();
            if (solicitud.getPersona().getCantHechosSubidos() == 0){
                // CAMBIAR ROL A VISUALIZADOR
                solicitud.getPersona().disminuirNivel();
            }

        }

    }
}
