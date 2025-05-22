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

    GestorRoles gestorRoles;

    @Autowired
    public SolicitudHechoService(ISolicitudAgregarHechoRepository solicitudAgregarHechoRepo, ISolicitudEliminarHechoRepository solicitudEliminarHechoRepo,
                                 IPersonaRepository personaRepository, IHechosRepository hechosRepository) {
        this.solicitudAgregarHechoRepo = solicitudAgregarHechoRepo;
        this.solicitudEliminarHechoRepo = solicitudEliminarHechoRepo;
        this.hechosRepository = hechosRepository;
        gestorRoles = new GestorRoles();
    }



    @Override
    public Integer solicitarSubirHecho(Hecho hecho, Usuario usuario) {
        // LA PERSONA DEBE SER O VISUALIZADORA O CONTRIBUYENTE
        SolicitudHecho solicitudHecho = new SolicitudHecho(usuario, hecho, solicitudAgregarHechoRepo.getProxId());
        solicitudAgregarHechoRepo.save(solicitudHecho);
        return HttpCode.OK.getCode();
    }

    //Si el campo Usuario es NULL significa que es anonimo
    @Override
    public Integer solicitarEliminacionHecho(Usuario usuario, Hecho hecho){
        if(usuario.getRol().equals(Rol.VISUALIZADOR)){
            return HttpCode.UNAUTHORIZED.getCode();
        }
        else {
            SolicitudHecho solicitud = new SolicitudHecho(usuario, hecho, solicitudEliminarHechoRepo.getProxId());
            solicitudEliminarHechoRepo.save(solicitud);
            return HttpCode.OK.getCode();
        }
    }

    @Override
    public Integer evaluarSolicitudSubirHecho(Usuario usuario, SolicitudHecho solicitud, Boolean respuesta) {
        if(!usuario.getRol().equals(Rol.ADMINISTRADOR)){
            return HttpCode.UNAUTHORIZED.getCode();
        }
        else {

            if (respuesta) {

                solicitud.getHecho().setActivo(true);
                solicitud.getUsuario().incrementarHechosSubidos();
                hechosRepository.save(solicitud.getHecho());

                if (solicitud.getUsuario().getRol().equals(Rol.VISUALIZADOR)){
                    gestorRoles.VisualizadorAContribuyente(solicitud.getUsuario());
                }
            }
            this.solicitudAgregarHechoRepo.delete(solicitud);
        }
        return HttpCode.OK.getCode();
    }

    @Override
    public Integer evaluarEliminacionHecho(Usuario usuario, SolicitudHecho solicitud, Boolean respuesta) {
        if(!usuario.getRol().equals(Rol.ADMINISTRADOR)){
            return HttpCode.UNAUTHORIZED.getCode();
        }
        else {
            if (respuesta) {

                // El hecho debería dejar de mostrarse en la página, pero NUNCA se borra por completo
                solicitud.getHecho().setActivo(false);
                solicitud.getUsuario().disminuirHechosSubidos();

                if (solicitud.getUsuario().getCantHechosSubidos() == 0){
                    gestorRoles.ContribuyenteAVisualizador(solicitud.getUsuario());
                }
            }

        }
        solicitudEliminarHechoRepo.delete(solicitud);
        return HttpCode.OK.getCode();
    }
}
