package services.impl;

import models.entities.Hecho;
import models.entities.SolicitudHecho;
import models.entities.personas.Persona;
import models.repositories.IRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import services.ISolicitudHechoService;

@Service
public class SolicitudHechoService implements ISolicitudHechoService {

    private final IRepository<SolicitudHecho> solicitudHechoRepo;

    @Autowired
    public SolicitudHechoService(IRepository<SolicitudHecho> solicitudHechoRepo) {
        this.solicitudHechoRepo = solicitudHechoRepo;
    }

    @Override
    public void solicitarSubirHecho(Hecho hecho, Persona persona) {
        // LA PERSONA DEBE SER O VISUALIZADORA O CONTRIBUYENTE
        SolicitudHecho solicitudHecho = new SolicitudHecho(persona, hecho);
        solicitudHechoRepo.save(solicitudHecho);
    }

    @Override
    public void evaluarSolicitudSubirHecho(SolicitudHecho solicitud, Boolean respuesta) {
        if(respuesta){
            solicitud.getHecho().setActivo(true);
            if(solicitud.getPersona().getNivel() == 0
                    && !solicitud.getPersona().getDatosPersonales().getNombre().isEmpty()){

                // Cambio el "rol" a contribuyente
                solicitud.getPersona().incrementarNivel();

                // TODO
                // Hay que hacer una lista de contribuyentes a la que solo tengan acceso los admins

            }
            solicitud.getPersona().incrementarHechosSubidos();
        }
        solicitudHechoRepo.delete(solicitud);
    }

    @Override
    public void evaluarEliminacionHecho(SolicitudHecho solicitud, Boolean respuesta) {
        if(respuesta){
            solicitudHechoRepo.delete(solicitud);
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
