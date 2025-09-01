package modulos.buscadores;

import modulos.shared.Categoria;
import modulos.shared.Hecho;
import modulos.shared.Pais;

import java.time.ZonedDateTime;
import java.util.List;

public class BuscadorHechoIdentico {
    // Vienen los hechos de origen estatico
    public static boolean existeHechoIdentico(Hecho hecho, List<Hecho> hechos){
        String tituloHechoNormalizado = Normalizador.normalizar(hecho.getAtributosHecho().getTitulo());
        Categoria categoria= hecho.getAtributosHecho().getCategoria();
        Pais pais = hecho.getAtributosHecho().getUbicacion().getPais();
        String descripcionNormalizada = Normalizador.normalizar(hecho.getAtributosHecho().getDescripcion());
        ZonedDateTime fechaAcontecimiento = hecho.getAtributosHecho().getFechaAcontecimiento();

        List<Hecho> hechosConTituloIgual = hechos.stream().filter(h-> Normalizador.normalizar(h.getAtributosHecho().getTitulo()).equals(tituloHechoNormalizado)).toList();

        for (Hecho h: hechosConTituloIgual){
            if (h.getAtributosHecho().getCategoria().equals(categoria) &&
                    h.getAtributosHecho().getUbicacion().getPais().equals(pais) &&
                    Normalizador.normalizar(h.getAtributosHecho().getDescripcion()).equals(descripcionNormalizada) &&
                    h.getAtributosHecho().getFechaAcontecimiento().equals(fechaAcontecimiento)){

                return true;
            }
        }

        return false;
    }
}
