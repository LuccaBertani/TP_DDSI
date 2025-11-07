package modulos.buscadores;

import modulos.agregacion.entities.DbDinamica.HechoDinamica;
import modulos.agregacion.entities.DbEstatica.HechoEstatica;
import modulos.agregacion.entities.DbMain.*;
import modulos.agregacion.entities.DbProxy.HechoProxy;
import modulos.agregacion.entities.atributosHecho.AtributosHecho;
import modulos.agregacion.repositories.DbDinamica.IHechosDinamicaRepository;
import modulos.agregacion.repositories.DbEstatica.IHechosEstaticaRepository;
import modulos.agregacion.repositories.DbMain.*;
import modulos.agregacion.repositories.DbProxy.IHechosProxyRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class BuscadorHecho {
    private final IHechosEstaticaRepository hechoRepoEstatica;
    private final IHechosDinamicaRepository hechoRepoDinamica;
    private final IHechosProxyRepository hechoRepoProxy;
    private final ICategoriaRepository categoriaRepository;
    private final IPaisRepository paisRepository;
    private final IProvinciaRepository provinciaRepository;
    private final IUbicacionRepository ubicacionRepository;


    public BuscadorHecho(IHechosEstaticaRepository hechoRepoEstatica, IHechosDinamicaRepository hechoRepoDinamica, IUbicacionRepository ubicacionRepository, IHechosProxyRepository hechoRepoProxy, ICategoriaRepository categoriaRepository, IPaisRepository paisRepository, IProvinciaRepository provinciaRepository) {
        this.hechoRepoEstatica = hechoRepoEstatica;
        this.hechoRepoDinamica = hechoRepoDinamica;
        this.hechoRepoProxy = hechoRepoProxy;
        this.categoriaRepository = categoriaRepository;
        this.paisRepository = paisRepository;
        this.provinciaRepository = provinciaRepository;
        this.ubicacionRepository = ubicacionRepository;
    }

    public Hecho buscarOCrearEstatica(String elemento){
        HechoEstatica hecho = this.buscarEstatica(elemento);
        if(hecho == null){
            hecho = new HechoEstatica();
            hecho.getAtributosHecho().setTitulo(elemento);
        }
        return hecho;
    }
    public Hecho buscarOCrearDinamica(String elemento){
        HechoDinamica hecho = this.buscarDinamica(elemento);
        if(hecho == null){
            hecho = new HechoDinamica();
            hecho.getAtributosHecho().setTitulo(elemento);
        }
        return hecho;
    }
    public Hecho buscarOCrearProxy(String elemento){
        HechoProxy hecho = this.buscarProxy(elemento);
        if(hecho == null){
            hecho = new HechoProxy();
            hecho.getAtributosHecho().setTitulo(elemento);
        }
        return hecho;
    }

    public HechoEstatica buscarEstatica(String elemento) {
        return this.hechoRepoEstatica.findByNombreNormalizado(elemento).orElse(null);
    }

    public List<HechoEstatica> buscarListaEstatica(String elemento) {
        return this.hechoRepoEstatica.findAllByNombreNormalizado(elemento);
    }

    public HechoDinamica buscarDinamica(String elemento) {
        return this.hechoRepoDinamica.findByNombreNormalizado(elemento).orElse(null);
    }

    public List<HechoDinamica> buscarListaDinamica(String elemento) {
        return this.hechoRepoDinamica.findAllByNombreNormalizado(elemento);
    }

    public HechoProxy buscarProxy(String elemento) {
        return this.hechoRepoProxy.findByNombreNormalizado(elemento).orElse(null);
    }

    public List<HechoProxy> buscarListaProxy(String elemento) {
        return this.hechoRepoProxy.findAllByNombreNormalizado(elemento);
    }

    public Long findCantDatasetsHecho(Long hecho_id){
        return this.hechoRepoEstatica.findCantDatasetsHecho(hecho_id);
    }

    public Long findCantHechosIgualTituloDiferentesAtributos(Long hecho_id){
        return this.hechoRepoEstatica.findCantHechosIgualTituloDiferentesAtributos(hecho_id);
    }


    private static boolean normEq(String a, String b) {
        // Evita NPE dentro del normalizador
        String s1 = (a == null) ? "" : a;
        String s2 = (b == null) ? "" : b;
        return Normalizador.normalizarYComparar(s1, s2);
    }

    // --- helpers null-safe ---
    private static boolean eqDouble(Double a, Double b, double eps) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        // también evita NaN/Infinity si querés:
        if (!Double.isFinite(a) || !Double.isFinite(b)) return false;
        return Math.abs(a - b) <= eps;
    }


    public HechoEstatica existeHechoIdentico(HechoEstatica hecho, Categoria categoria, Pais pais, Provincia provincia, List<HechoEstatica> hechosASubir) {
        var tgtAttr = Optional.ofNullable(hecho).map(Hecho::getAtributosHecho);

        if (hecho != null && hecho.getAtributosHecho() != null) {
            String tituloTgt = tgtAttr.map(AtributosHecho::getTitulo).orElse(null);
            String catTgt = categoria != null ? categoria.getTitulo() : null;
            String paisTgt  = pais != null ? pais.getPais() : null;
            String provTgt = provincia != null ? provincia.getProvincia() : null;
            String descTgt   = tgtAttr.map(AtributosHecho::getDescripcion).orElse(null);
            var    fechaTgt  = tgtAttr.map(AtributosHecho::getFechaAcontecimiento).orElse(null);
            Double latTgt    = tgtAttr.map(AtributosHecho::getLatitud).orElse(null);
            Double lonTgt    = tgtAttr.map(AtributosHecho::getLongitud).orElse(null);

            List<HechoEstatica> hechos = this.hechoRepoEstatica.findAllByNombreNormalizado(tituloTgt);
            hechos.addAll(hechosASubir);

            final double EPS_DEG = 1e-6; // ~0.11 m en el ecuador; subilo a 1e-5 si querés ~1.1 m

            for (HechoEstatica h : hechos) {
                var attr  = Optional.ofNullable(h).map(Hecho::getAtributosHecho);

                String desc  = attr.map(AtributosHecho::getDescripcion).orElse(null);
                var    fecha = attr.map(AtributosHecho::getFechaAcontecimiento).orElse(null);
                Double lat   = attr.map(AtributosHecho::getLatitud).orElse(null);
                Double lon   = attr.map(AtributosHecho::getLongitud).orElse(null);

                String cat = h.getAtributosHecho().getCategoria_id()!=null ? categoriaRepository.findById(h.getAtributosHecho().getCategoria_id()).orElse(null).getTitulo() : null;
                Ubicacion ubicacion = ubicacionRepository.findById(h.getAtributosHecho().getUbicacion_id()).orElse(null);

                String paisStr = ubicacion.getPais() != null ? ubicacion.getPais().getPais() : null;
                String prov = ubicacion.getProvincia() != null ? ubicacion.getProvincia().getProvincia() : null;

                boolean mismaCat   = normEq(cat,  catTgt);
                boolean mismoPais  = normEq(paisStr, paisTgt);
                boolean mismaProvincia = normEq(prov,provTgt);
                boolean mismaDesc  = normEq(desc, descTgt);
                boolean mismaFecha = Objects.equals(fecha, fechaTgt);


                boolean mismaLat = false;
                boolean mismaLon = false;

                if (lat!=null){
                    if (lat.equals(latTgt)){
                        mismaLat = true;
                    }
                }

                if (lon!=null){
                    if (lon.equals(lonTgt)){
                        mismaLon = true;
                    }
                }

                if (mismaCat && mismoPais && mismaProvincia && mismaDesc && mismaFecha && mismaLat && mismaLon) {
                    return h;
                }
            }
        }
        return null;
    }

}
