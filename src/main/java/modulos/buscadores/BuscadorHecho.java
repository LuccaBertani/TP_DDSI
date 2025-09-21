package modulos.buscadores;

import modulos.agregacion.entities.DbDinamica.HechoDinamica;
import modulos.agregacion.entities.DbEstatica.HechoEstatica;
import modulos.agregacion.entities.DbMain.*;
import modulos.agregacion.entities.DbProxy.HechoProxy;
import modulos.agregacion.entities.atributosHecho.AtributosHecho;
import modulos.agregacion.repositories.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class BuscadorHecho {
    private final IHechosEstaticaRepository hechoRepoEstatica;
    private final IHechosDinamicaRepository hechoRepoDinamica;
    private final IHechosProxyRepository hechoRepoProxy;
    private final IHechoRepository hechosRepo;


    public BuscadorHecho(IHechosEstaticaRepository hechoRepoEstatica, IHechosDinamicaRepository hechoRepoDinamica, IHechosProxyRepository hechoRepoProxy, IHechoRepository hechosRepo) {
        this.hechoRepoEstatica = hechoRepoEstatica;
        this.hechoRepoDinamica = hechoRepoDinamica;
        this.hechoRepoProxy = hechoRepoProxy;
        this.hechosRepo = hechosRepo;
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


    public Hecho existeHechoIdentico(HechoEstatica hecho) {
        var tgtAttr = Optional.ofNullable(hecho).map(Hecho::getAtributosHecho);

        if (hecho != null && hecho.getAtributosHecho() != null) {
            String tituloTgt = tgtAttr.map(AtributosHecho::getTitulo).orElse(null);
            String catTgt    = tgtAttr.map(AtributosHecho::getCategoria).map(Categoria::getTitulo).orElse(null);
            String paisTgt   = tgtAttr.map(AtributosHecho::getUbicacion).map(Ubicacion::getPais).map(Pais::getPais).orElse(null);
            String descTgt   = tgtAttr.map(AtributosHecho::getDescripcion).orElse(null);
            var    fechaTgt  = tgtAttr.map(AtributosHecho::getFechaAcontecimiento).orElse(null);
            Double latTgt    = tgtAttr.map(AtributosHecho::getLatitud).orElse(null);
            Double lonTgt    = tgtAttr.map(AtributosHecho::getLongitud).orElse(null);

            List<HechoEstatica> hechos = this.hechoRepoEstatica.findAllByNombreNormalizado(tituloTgt);

            final double EPS_DEG = 1e-6; // ~0.11 m en el ecuador; subilo a 1e-5 si querés ~1.1 m

            for (Hecho h : hechos) {
                var attr  = Optional.ofNullable(h).map(Hecho::getAtributosHecho);
                String cat   = attr.map(AtributosHecho::getCategoria).map(Categoria::getTitulo).orElse(null);
                String pais  = attr.map(AtributosHecho::getUbicacion).map(Ubicacion::getPais).map(Pais::getPais).orElse(null);
                String desc  = attr.map(AtributosHecho::getDescripcion).orElse(null);
                var    fecha = attr.map(AtributosHecho::getFechaAcontecimiento).orElse(null);
                Double lat   = attr.map(AtributosHecho::getLatitud).orElse(null);
                Double lon   = attr.map(AtributosHecho::getLongitud).orElse(null);

                boolean mismaCat   = normEq(cat,  catTgt);
                boolean mismoPais  = normEq(pais, paisTgt);
                boolean mismaDesc  = normEq(desc, descTgt);
                boolean mismaFecha = Objects.equals(fecha, fechaTgt);
                boolean mismaLat   = eqDouble(lat, latTgt, EPS_DEG);
                boolean mismaLon   = eqDouble(lon, lonTgt, EPS_DEG);

                if (mismaCat && mismoPais && mismaDesc && mismaFecha && mismaLat && mismaLon) {
                    return h;
                }
            }
        }
        return null;
    }

}
