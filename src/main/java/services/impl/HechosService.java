package services.impl;

import models.dtos.input.ImportacionHechosInputDTO;
import models.dtos.input.SolicitudHechoInputDTO;
import models.entities.*;
import models.entities.casosDeUso.NavegarPorHechos;
import models.entities.filtros.Filtro;
import models.entities.fuentes.Fuente;
import models.entities.fuentes.FuenteEstatica;
import models.entities.personas.Rol;
import models.entities.personas.Usuario;
import models.repositories.IHechosRepository;
import models.repositories.IPersonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import services.IHechosService;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class HechosService implements IHechosService {


    private final IHechosRepository hechosRepo;
    private final IPersonaRepository usuariosRepo;
    public HechosService(IHechosRepository repo, IPersonaRepository usuariosRepo) {
        this.hechosRepo = repo;
        this.usuariosRepo = usuariosRepo;
    }

    @Override
    public RespuestaHttp<Integer> subirHecho(SolicitudHechoInputDTO dtoInput) {
        Usuario usuario = usuariosRepo.findById(dtoInput.getId_usuario());
        if(usuario.getRol().equals(Rol.ADMINISTRADOR)){

            // TODO: METER ESTE ALGORITMO QUE CHEQUEA PAIS EXISTENTE Y CREA UN HECHO DESDE 0 EN UN METODO APARTE
            Hecho hecho = new Hecho();

            List<Hecho> hechos = hechosRepo.findAll(); // TODO cambiar x la temporal

            Optional<Hecho> hecho2 = hechos.stream().filter(h->Normalizador.normalizarYComparar(h.getPais().getPais(), dtoInput.getPais())).findFirst();
            Pais pais;

            if (hecho2.isPresent()){
                pais = hecho2.get().getPais();
            } else {
                pais = new Pais();
                pais.setPais(dtoInput.getPais());
            }

            hecho.setTitulo(dtoInput.getTitulo());
            hecho.setDescripcion(dtoInput.getDescripcion());
            hecho.setPais(pais);
            hecho.setFechaAcontecimiento(FechaParser.parsearFecha(dtoInput.getFechaAcontecimiento()));
            hecho.setOrigen(Origen.CARGA_MANUAL);
            hecho.setId(hechosRepo.getProxId());
            hecho.setActivo(true);
            hecho.setFechaDeCarga(ZonedDateTime.now());
            hechosRepo.save(hecho);
            return new RespuestaHttp<>(-1, HttpStatus.OK.value());
        }
        else{
            return new RespuestaHttp<>(-1, HttpStatus.UNAUTHORIZED.value());
        }
    }

    @Override
    public RespuestaHttp<Integer> importarHechos(ImportacionHechosInputDTO dtoInput){
        Usuario usuario = usuariosRepo.findById(dtoInput.getId_usuario());
        if (usuario.getRol().equals(Rol.ADMINISTRADOR)){
            // Se borran y suben hechos constantemente => Guardamos los que se tienen hasta el momento en una lista
            List<Hecho> hechosActuales = hechosRepo.findAll();
            FuenteEstatica fuente = new FuenteEstatica();
            fuente.setDataSet(dtoInput.getFuenteString());
            ModificadorHechos modificadorHechos = fuente.leerFuente(hechosActuales);

            List<Hecho> hechosASubir = modificadorHechos.getHechosASubir();
            List<Hecho> hechosAModificar = modificadorHechos.getHechosAModificar();

            for (Hecho hecho : hechosASubir){
                hecho.setId(hechosRepo.getProxId());
                hechosRepo.save(hecho);
            }
            for (Hecho hecho : hechosAModificar){
                hechosRepo.update(hecho);
            }
            return new RespuestaHttp<>(-1, HttpStatus.OK.value());
        }

        return new RespuestaHttp<>(-1, HttpStatus.UNAUTHORIZED.value());

    }

    @Override
    public void navegarPorHechos(List<Filtro> filtros, Coleccion coleccion){
        var objetoNavegarPorHechos = new NavegarPorHechos();
        objetoNavegarPorHechos.navegarPorHechos(filtros,coleccion);
    }

    @Override
    public void navegarPorHechos(Coleccion coleccion){
        var objetoNavegarPorHechos = new NavegarPorHechos();
        objetoNavegarPorHechos.navegarPorHechos(coleccion);
    }




}
