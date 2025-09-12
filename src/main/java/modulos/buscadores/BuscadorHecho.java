package modulos.buscadores;

import modulos.agregacion.entities.*;
import modulos.agregacion.repositories.*;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class BuscadorHecho {
    private final IHechosEstaticaRepository hechoRepoEstatica;
    private final IHechosDinamicaRepository hechoRepoDinamica;
    private final IHechosProxyRepository hechoRepoProxy;

    public BuscadorHecho(IHechosEstaticaRepository hechoRepoEstatica, IHechosDinamicaRepository hechoRepoDinamica, IHechosProxyRepository hechoRepoProxy) {
        this.hechoRepoEstatica = hechoRepoEstatica;
        this.hechoRepoDinamica = hechoRepoDinamica;
        this.hechoRepoProxy = hechoRepoProxy;
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

    public Hecho existeHechoIdentico(HechoEstatica hecho) {

        var tgtAttr = Optional.ofNullable(hecho)
                .map(Hecho::getAtributosHecho);

        String tituloTgt   = tgtAttr.map(AtributosHecho::getTitulo).orElse(null);
        String catTgt      = tgtAttr.map(AtributosHecho::getCategoria)
                .map(Categoria::getTitulo).orElse(null);
        String paisTgt     = tgtAttr.map(AtributosHecho::getUbicacion)
                .map(Ubicacion::getPais)
                .map(Pais::getPais).orElse(null);
        String descTgt     = tgtAttr.map(AtributosHecho::getDescripcion).orElse(null);
        var    fechaTgt    = tgtAttr.map(AtributosHecho::getFechaAcontecimiento).orElse(null);

        List<HechoEstatica> hechos =
                this.hechoRepoEstatica.findAllByNombreNormalizado(tituloTgt);

        for (Hecho h : hechos) {
            var attr = Optional.ofNullable(h).map(Hecho::getAtributosHecho);

            String cat    = attr.map(AtributosHecho::getCategoria)
                    .map(Categoria::getTitulo).orElse(null);
            String pais   = attr.map(AtributosHecho::getUbicacion)
                    .map(Ubicacion::getPais)
                    .map(Pais::getPais).orElse(null);
            String desc   = attr.map(AtributosHecho::getDescripcion).orElse(null);
            var    fecha  = attr.map(AtributosHecho::getFechaAcontecimiento).orElse(null);

            boolean mismaCat   = normEq(cat,  catTgt);
            boolean mismoPais  = normEq(pais, paisTgt);
            boolean mismaDesc  = normEq(desc, descTgt);
            boolean mismaFecha = Objects.equals(fecha, fechaTgt); // maneja nulls

            if (mismaCat && mismoPais && mismaDesc && mismaFecha) {
                return h;
            }
        }

        return null;
    }


}
