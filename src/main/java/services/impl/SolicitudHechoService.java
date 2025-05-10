package services.impl;

import models.entities.*;
import models.entities.personas.Rol;
import models.entities.personas.Usuario;
import models.repositories.IHechosRepository;
import models.repositories.IPersonaRepository;
import models.repositories.ISolicitudAgregarHechoRepository;
import models.repositories.ISolicitudEliminarHechoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import services.ISolicitudHechoService;

@Service
public class SolicitudHechoService implements ISolicitudHechoService {

    private final ISolicitudAgregarHechoRepository solicitudAgregarHechoRepo;
    private final ISolicitudEliminarHechoRepository solicitudEliminarHechoRepo;
    private final IHechosRepository hechosRepository;

    GestorRoles gestorRoles = new GestorRoles();

    @Autowired
    public SolicitudHechoService(ISolicitudAgregarHechoRepository solicitudAgregarHechoRepo, ISolicitudEliminarHechoRepository solicitudEliminarHechoRepo,
                                 IPersonaRepository personaRepository, IHechosRepository hechosRepository) {
        this.solicitudAgregarHechoRepo = solicitudAgregarHechoRepo;
        this.solicitudEliminarHechoRepo = solicitudEliminarHechoRepo;
        this.hechosRepository = hechosRepository;
    }

    //Si el campo Usuario es NULL significa que es anonimo
    @Override
    public void solicitarSubirHecho(Hecho hecho, Usuario usuario) {
        // LA PERSONA DEBE SER O VISUALIZADORA O CONTRIBUYENTE
        SolicitudHecho solicitudHecho = new SolicitudHecho(usuario, hecho, solicitudAgregarHechoRepo.getProxId());
        solicitudAgregarHechoRepo.save(solicitudHecho);
    }

    //Si el campo Usuario es NULL significa que es anonimo
    @Override
    public void solicitarEliminacionHecho(Usuario usuario, Hecho hecho){

        if(usuario.getRol().equals(Rol.VISUALIZADOR)){
            throw new SecurityException("No tiene permisos para ejecutar el caso de uso");
        }
        else {
            SolicitudHecho solicitud = new SolicitudHecho(usuario, hecho, solicitudEliminarHechoRepo.getProxId());

            solicitudEliminarHechoRepo.save(solicitud);
        }
    }

    @Override
    public void evaluarSolicitudSubirHecho(Usuario usuario, SolicitudHecho solicitud, Boolean respuesta) {
        if(!usuario.getRol().equals(Rol.ADMINISTRADOR)){

            throw new SecurityException("No tiene permisos para ejecutar el caso de uso");

        }
        else {

            if (respuesta) {

                solicitud.getHecho().setActivo(true);

                hechosRepository.save(solicitud.getHecho());

                if(gestorRoles.VisualizadorAContribuyente(solicitud.getUsuario())){

                    solicitud.getUsuario().incrementarHechosSubidos();

                }

            }
            this.solicitudAgregarHechoRepo.delete(solicitud);
        }
    }

    @Override
    public void evaluarEliminacionHecho(Usuario usuario, SolicitudHecho solicitud, Boolean respuesta) {
        if(!usuario.getRol().equals(Rol.ADMINISTRADOR)){

            throw new SecurityException("No tiene permisos para ejecutar el caso de uso");

        }
        else {
            if (respuesta) {

                // El hecho debería dejar de mostrarse en la página, pero NUNCA se borra por completo
                solicitud.getHecho().setActivo(false);

                solicitud.getUsuario().disminuirHechosSubidos();

                gestorRoles.ContribuyenteAVisualizador(solicitud.getUsuario());

            }

        }
        solicitudEliminarHechoRepo.delete(solicitud);
    }
}
