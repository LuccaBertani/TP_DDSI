package services.impl;

import models.entities.*;
import models.entities.filtros.Filtro;
import models.entities.personas.Rol;
import models.entities.personas.Usuario;
import models.repositories.IColeccionRepository;
import models.repositories.IHechosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import services.IColeccionService;

import java.util.List;

@Service
public class ColeccionService implements IColeccionService {

    private final IHechosRepository hechosRepo;
    private final IColeccionRepository coleccionesRepo;

    @Autowired
    public ColeccionService(IHechosRepository hechosRepo, IColeccionRepository coleccionesRepo) {
        this.hechosRepo = hechosRepo;
        this.coleccionesRepo = coleccionesRepo;
    }


    /*
    Colecciones
Las colecciones representan conjuntos de hechos. Las mismas pueden ser consultadas por cualquier persona, de forma
pública, y no pueden ser editadas ni eliminadas manualmente (esto último, con una sola excepción, ver más adelante).

Las colecciones tienen un título, como por ejemplo “Desapariciones vinculadas a crímenes de odio”, o “Incendios
forestales en Argentina 2025” y una descripción. Las personas administradoras pueden crear tantas colecciones como deseen.

Las colecciones están asociadas a una fuente y tomarán los hechos de las mismas: para esto las colecciones también contarán con un criterio de
pertenencia configurable, que dictará si un hecho pertenece o no a las mismas. Por ejemplo, la colección de “Incendios forestales…” deberá
incluir automáticamente todos los hechos de categoría “Incendio forestal” ocurrido en Argentina, acontecido entre el 1 de enero de 2025 a las
0:00 y el 31 de diciembre de 20205 a las 23:59.

    */

    @Override
    public RespuestaHttp<Integer> crearColeccion(List<Filtro> criterios, DatosColeccion datos, Usuario usuario) {

        if (usuario.getRol().equals(Rol.ADMINISTRADOR)) {

            Coleccion coleccion = new Coleccion(datos,coleccionesRepo.getProxId());
            coleccion.addCriterios(criterios);
            List<Hecho> hechos = hechosRepo.findAll();
            Filtrador filtrador = new Filtrador();

            coleccion.addHechos(filtrador.aplicarFiltros(criterios, hechos));

            coleccionesRepo.save(coleccion);

            return new RespuestaHttp<>(-1, HttpCode.OK.getCode());

        } else {
            return new RespuestaHttp<>(-1, HttpCode.UNAUTHORIZED.getCode());
        }
    }
}
