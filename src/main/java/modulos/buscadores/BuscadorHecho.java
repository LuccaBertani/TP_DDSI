package modulos.buscadores;

import modulos.agregacion.entities.*;
import modulos.agregacion.repositories.*;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;

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


    public Hecho existeHechoIdentico(HechoEstatica hecho){


        List<HechoEstatica> hechos = this.hechoRepoEstatica.findAllByNombreNormalizado(hecho.getAtributosHecho().getTitulo());

        for (Hecho h: hechos){
            if (Normalizador.normalizarYComparar(h.getAtributosHecho().getCategoria().getTitulo(), hecho.getAtributosHecho().getCategoria().getTitulo()) &&
                    Normalizador.normalizarYComparar(h.getAtributosHecho().getUbicacion().getPais().getPais(),hecho.getAtributosHecho().getUbicacion().getPais().getPais()) &&
                    Normalizador.normalizarYComparar(h.getAtributosHecho().getDescripcion(),hecho.getAtributosHecho().getDescripcion()) &&
                    h.getAtributosHecho().getFechaAcontecimiento().equals(hecho.getAtributosHecho().getFechaAcontecimiento())) {
                return h;
            }
        }

        return null;
    }

}
