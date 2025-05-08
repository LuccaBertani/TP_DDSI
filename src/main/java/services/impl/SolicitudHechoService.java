package services.impl;

import models.entities.*;
import models.entities.personas.Rol;
import models.entities.personas.Usuario;
import models.repositories.IMemoriaPersonaRepository;
import models.repositories.IMemoriaSolicitudAgregarHechoRepository;
import models.repositories.IMemoriaSolicitudEliminarHechoRepository;
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
    public void solicitarSubirHecho(Hecho hecho, Usuario usuario) {
        // LA PERSONA DEBE SER O VISUALIZADORA O CONTRIBUYENTE
        SolicitudHecho solicitudHecho = new SolicitudHecho(usuario, hecho);
        solicitudAgregarHechoRepo.save(solicitudHecho);
    }

    @Override
    public void solicitarEliminacionHecho(Usuario usuario, Hecho hecho){

        SolicitudHecho solicitud = new SolicitudHecho(usuario, hecho);

        solicitudEliminarHechoRepo.save(solicitud);
    }

    @Override
    public void evaluarSolicitudSubirHecho(SolicitudHecho solicitud, Boolean respuesta) {
        if(respuesta){
            solicitud.getHecho().setActivo(true);
            if(solicitud.getUsuario().getRol().equals(Rol.VISUALIZADOR)
                    && !solicitud.getUsuario().getDatosPersonales().getNombre().isEmpty()){

                // Cambio el "rol" a contribuyente
                solicitud.getUsuario().setRol(Rol.CONTRIBUYENTE);

                this.personaRepository.save(solicitud.getUsuario());

            }
            solicitud.getUsuario().incrementarHechosSubidos();
        }
        this.solicitudAgregarHechoRepo.delete(solicitud);
    }

    @Override
    public void evaluarEliminacionHecho(SolicitudHecho solicitud, Boolean respuesta) {
        if(respuesta){
            solicitudEliminarHechoRepo.delete(solicitud);
            // El hecho deberia dejar de mostrarse en la pagina, pero NUNCA se borra por completo
            solicitud.getHecho().setActivo(false);

            solicitud.getUsuario().disminuirHechosSubidos();
            if (solicitud.getUsuario().getCantHechosSubidos() == 0){
                // CAMBIAR ROL A VISUALIZADOR
                solicitud.getUsuario().setRol(Rol.VISUALIZADOR);
            }

        }

    }
}
