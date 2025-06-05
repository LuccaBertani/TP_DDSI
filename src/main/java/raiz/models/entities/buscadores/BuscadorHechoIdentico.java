package raiz.models.entities.buscadores;

import raiz.models.entities.Categoria;
import raiz.models.entities.Hecho;
import raiz.models.entities.Normalizador;
import raiz.models.entities.Pais;

import java.time.ZonedDateTime;
import java.util.List;

public class BuscadorHechoIdentico {
    // Vienen los hechos de origen estatico
    public static boolean existeHechoIdentico(Hecho hecho, List<Hecho> hechos){
        String tituloHechoNormalizado = Normalizador.normalizar(hecho.getTitulo());
        Categoria categoria= hecho.getCategoria();
        Pais pais = hecho.getPais();
        String descripcionNormalizada = Normalizador.normalizar(hecho.getDescripcion());
        ZonedDateTime fechaAcontecimiento = hecho.getFechaAcontecimiento();

        List<Hecho> hechosConTituloIgual = hechos.stream().filter(h-> Normalizador.normalizar(h.getTitulo()).equals(tituloHechoNormalizado)).toList();

        for (Hecho h: hechosConTituloIgual){
            if (h.getCategoria().equals(categoria) &&
                    h.getPais().equals(pais) &&
                    Normalizador.normalizar(h.getDescripcion()).equals(descripcionNormalizada) &&
                    h.getFechaAcontecimiento().equals(fechaAcontecimiento)){

                return true;
            }
        }

        return false;
    }
}
