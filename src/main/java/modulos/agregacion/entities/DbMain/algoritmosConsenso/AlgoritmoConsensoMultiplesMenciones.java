package modulos.agregacion.entities.DbMain.algoritmosConsenso;

import modulos.agregacion.entities.atributosHecho.AtributosHecho;
import modulos.agregacion.entities.DbMain.Coleccion;
import modulos.agregacion.entities.DbMain.Ubicacion;
import modulos.agregacion.entities.DbEstatica.Dataset;
import modulos.agregacion.entities.DbMain.Hecho;
import modulos.buscadores.Normalizador;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AlgoritmoConsensoMultiplesMenciones implements IAlgoritmoConsenso {

    // múltiples menciones: si al menos dos fuentes contienen un mismo hecho y ninguna
    // otra fuente contiene otro de igual título pero diferentes atributos, se lo considera consensuado

    // Por la forma en la que tenemos implementadas las fuentes no hay necesidad de usar la lista de fuentes "global"
    @Override
    public void ejecutarAlgoritmoConsenso(List<Dataset> fuentes, Coleccion coleccion) {

        List<Hecho> hechos = coleccion.getHechos();
        List<Hecho> hechosConsensuados = hechos.stream().filter(hecho->
                !coleccion.getHechosConsensuados().contains(hecho) &&
                this.dosFuentesContienenHecho(hecho) &&
                !this.existeHechoIgualTituloDiferentesAtributos(hecho, hechos)).toList();

        coleccion.getHechosConsensuados().addAll(hechosConsensuados);
    }

    private boolean dosFuentesContienenHecho(Hecho hecho){
        return hecho.getDatasets().size() >= 2;
    }

    // No hay necesidad de hacer chequeo de fuentes
    private boolean existeHechoIgualTituloDiferentesAtributos(Hecho hecho, List<Hecho> hechos){
        Hecho hechoRep = hechos.stream().filter
                        (h-> Normalizador.normalizarYComparar(h.getAtributosHecho().getTitulo(), hecho.getAtributosHecho().getTitulo()) &&
                                this.tienenAtributosDistintos(hecho, h)).
                                findFirst().orElse(null);

        return hechoRep!=null;
    }

    private boolean tienenAtributosDistintos(Hecho h1, Hecho h2) {
        var a1 = Optional.ofNullable(h1.getAtributosHecho());
        var a2 = Optional.ofNullable(h2.getAtributosHecho());

        // descripcion: si es null, comparo como "" para que el normalizador no reciba null
        String d1 = a1.map(AtributosHecho::getDescripcion).orElse("");
        String d2 = a2.map(AtributosHecho::getDescripcion).orElse("");
        boolean descDistinta = !Normalizador.normalizarYComparar(d1, d2);

        // categoria (puede ser null)
        boolean categoriaDistinta = !Objects.equals(
                a1.map(AtributosHecho::getCategoria).orElse(null),
                a2.map(AtributosHecho::getCategoria).orElse(null)
        );

        // pais (ubicacion puede ser null)
        var pais1 = a1.map(AtributosHecho::getUbicacion).map(Ubicacion::getPais).orElse(null);
        var pais2 = a2.map(AtributosHecho::getUbicacion).map(Ubicacion::getPais).orElse(null);
        boolean paisDistinto = !Objects.equals(pais1, pais2);

        // fecha (puede ser null)
        var f1 = a1.map(AtributosHecho::getFechaAcontecimiento).orElse(null);
        var f2 = a2.map(AtributosHecho::getFechaAcontecimiento).orElse(null);
        boolean fechaDistinta = !Objects.equals(f1, f2);

        // provincia (ubicacion puede ser null)
        var prov1 = a1.map(AtributosHecho::getUbicacion).map(Ubicacion::getProvincia).orElse(null);
        var prov2 = a2.map(AtributosHecho::getUbicacion).map(Ubicacion::getProvincia).orElse(null);
        boolean provinciaDistinta = !Objects.equals(prov1, prov2);

        var lat1 = a1.map(AtributosHecho::getLatitud).orElse(null);
        var lat2 = a2.map(AtributosHecho::getLongitud).orElse(null);

        var lon1 = a1.map(AtributosHecho::getLongitud).orElse(null);
        var lon2 = a2.map(AtributosHecho::getLongitud).orElse(null);

        boolean latitudDistinta = !Objects.equals(lat1, lat2);
        boolean longitudDistinta = !Objects.equals(lon1, lon2);

        return descDistinta || categoriaDistinta || paisDistinto || fechaDistinta || provinciaDistinta || latitudDistinta || longitudDistinta;
    }
}
