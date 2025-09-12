package modulos.apis;

import modulos.agregacion.entities.*;
import modulos.agregacion.entities.projections.*;
import modulos.agregacion.repositories.ICategoriaRepository;
import modulos.agregacion.repositories.IColeccionRepository;
import modulos.agregacion.repositories.IProvinciaRepository;
import modulos.agregacion.repositories.ISolicitudEliminarHechoRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DatosQuery implements IDatosQuery{

    IColeccionRepository repoColeccion;
    IProvinciaRepository repoProvincia;
    ICategoriaRepository repoCategoria;
    ISolicitudEliminarHechoRepository repoSoliElimHecho;

    public DatosQuery(IColeccionRepository repoColeccion, IProvinciaRepository repoProvincia, ICategoriaRepository repoCategoria, ISolicitudEliminarHechoRepository repoSoliElimHecho){
        this.repoColeccion = repoColeccion;
        this.repoProvincia = repoProvincia;
        this.repoCategoria = repoCategoria;
        this.repoSoliElimHecho = repoSoliElimHecho;
    }

    @Override
    public List<ColeccionProvincia> obtenerMayorCantHechosProvinciaEnColeccion() {
        List<ColeccionProvinciaProjection> info = repoColeccion.obtenerMayorCantHechosProvinciaEnColeccion();

        List<ColeccionProvincia> infos = new ArrayList<>();

        for(ColeccionProvinciaProjection cpp : info){

            Coleccion coleccion = cpp.getColeccionId() != null ? repoColeccion.findById(cpp.getColeccionId()).orElse(null) : null;
            Provincia provincia = cpp.getProvinciaId() != null ? repoProvincia.findById(cpp.getProvinciaId()).orElse(null) : null;

            ColeccionProvincia coleccionProvincia = new ColeccionProvincia(coleccion, provincia, cpp.getTotalHechos());
            infos.add(coleccionProvincia);
        }

        return infos;
    }

    @Override
    public CategoriaCantidad mayorCantHechosCategoria() {
        CategoriaCantidadProjection info = repoCategoria.obtenerColeccionMayorHechos();
        // null pointer exception cuando info es null
        Categoria categoria = info.getCategoriaId() != null ? repoCategoria.findById(info.getCategoriaId()).orElse(null) : null;
        return new CategoriaCantidad(categoria,info.getCantHechos());
    }

    @Override
    public List<CategoriaProvincia> obtenerMayorCantHechosProvincia() {
        List<CategoriaProvinciaProjection> info = repoProvincia.obtenerCategoriaMayorHechosProvincia();

        List<CategoriaProvincia> infos = new ArrayList<>();

        for(CategoriaProvinciaProjection cpp : info){
            Categoria categoria = cpp.getCategoriaId() != null ? repoCategoria.findById(cpp.getCategoriaId()).orElse(null) : null;
            Provincia provincia = cpp.getProvinciaId() != null ? repoProvincia.findById(cpp.getProvinciaId()).orElse(null) : null;
            CategoriaProvincia categoriaProvincia = new CategoriaProvincia(categoria,provincia,cpp.getCantHechos());
            infos.add(categoriaProvincia);
        }
        return infos;
    }

    @Override
    public List<CategoriaHora> horaMayorCantHechos() {
        List<HoraCategoriaProjection> info = repoCategoria.obtenerHoraMaxHechosCategoria();

        List<CategoriaHora> infos = new ArrayList<>();

        for(HoraCategoriaProjection cpp : info){
            Categoria categoria = cpp.getIdCategoria() != null ? repoCategoria.findById(cpp.getIdCategoria()).orElse(null) : null;
            CategoriaHora categoriaHora = new CategoriaHora(categoria,cpp.getHoraDelDia(),cpp.getTotalHechos());
            infos.add(categoriaHora);
        }
        return infos;
    }

    @Override
    public CantSolicitudesEliminacionSpam cantSolicitudesEliminacionSpam() {
        CantSolicitudesSpamProjection info = repoSoliElimHecho.obtenerCantSolicitudesEliminacionSpam();
        return new CantSolicitudesEliminacionSpam(info.getTotalSpam());
    }
}

/*
-- De una colección, ¿en qué provincia se agrupan la mayor cantidad de hechos reportados?

SELECT
p.id         AS provincia_id,
p.nombre     AS provincia,
COUNT(*)     AS total_hechos
FROM coleccion c
JOIN coleccion_hecho ch ON ch.coleccion_id = c.id
JOIN hecho h            ON h.id = ch.hecho_id
JOIN ubicacion u        ON u.id = h.ubicacion_id
JOIN provincia p        ON p.id = h.provincia_id
WHERE c.id = ?                      -- ← id de la colección
GROUP BY p.id, p.nombre
ORDER BY total_hechos DESC;

        -- ¿Cuál es la categoría con mayor cantidad de hechos reportados?

SELECT *
FROM coleccion as c
JOIN coleccion_hecho as ch ON ch.coleccion_id = c.id
JOIN hecho as h ON c.id = ch.hecho_id
ORDER BY COUNT(h.id) DESC
LIMIT 1;

        -- ¿En qué provincia se presenta la mayor cantidad de hechos de una cierta categoría?

SELECT p.id, p.nombre, COUNT(h.id) AS cant_hechos
FROM provincia as p
JOIN ubicacion AS u ON u.provincia_id = p.id
JOIN hecho AS h ON h.ubicacion_id = u.id
WHERE h.categoria_id = categoriaId -- variable que se trae de java
GROUP BY p.id, p.nombre
ORDER BY cant_hechos DESC
LIMIT 1;

        -- ¿A qué hora del día ocurren la mayor cantidad de hechos de una cierta categoría?

SELECT
HOUR(h.fecha_hora) AS hora_del_dia,
COUNT(*)           AS total
FROM hecho h
WHERE h.categoria_id = categoriaId -- variable que se trae de java
GROUP BY HOUR(h.fecha_hora)
ORDER BY total DESC
LIMIT 1;

        -- ¿Cuántas solicitudes de eliminación son spam?

SELECT COUNT (s.id) as total_spam
FROM solicitud_hecho AS s
WHERE s.rechazada_por_spam = 1 AND s.tipoSolicitud = 'SOLICITUD_ELIMINAR';
*/