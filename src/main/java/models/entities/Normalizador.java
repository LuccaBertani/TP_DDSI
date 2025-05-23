package models.entities;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;

public class Normalizador {

    public static String normalizar(String texto) {
        // Quitar tildes
        String sinTildes = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        // Quitar espacios y pasar a minúsculas
        return sinTildes.replaceAll("\\s+", "").toLowerCase();
    }

    public static List<String> normalizarSeparado(String texto) {
        return Arrays.stream(texto.toLowerCase().split("\\s+"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }


    public static Boolean normalizarYComparar(String s1, String s2){
        s1=normalizar(s1);
        s2=normalizar(s2);

        return s1.equals(s2);
    }
}
