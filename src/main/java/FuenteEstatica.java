import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class FuenteEstatica implements Fuente{
    @Getter
    @Setter
    private String dataSet;
    public List<Hecho> leerFuente(){

        String linea;
        List<Hecho> listaHechos = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(this.dataSet), Charset.forName("ISO-8859-1"))))
        {

            linea = br.readLine(); // Me salteo la primera fila
            while ((linea = br.readLine()) != null) {
                String[] valores = linea.split(";");

                String titulo = valores[0];
                String descripcion = valores[1];
                String categoria = valores[2];
                Double latitud = Double.parseDouble(valores[3]);
                Double longitud = Double.parseDouble(valores[4]);

                String nombrePais = Geocodificador.obtenerPais(latitud, longitud);

                Optional<Hecho> hecho = Globales.hechosTotales.stream().filter(hecho->hecho.getPais().getPais() == nombrePais).findFirst();
                Pais pais;
                if (hecho.isEmpty()){
                    pais = new Pais();
                    pais.setPais(nombrePais);
                }
                else{
                    pais = hecho.get().getPais();
                }

                LocalDate fechaAcontecimiento = LocalDate.parse(valores[5],formatter);
                for (String valor : valores) {
                    System.out.print(valor + " | ");
                }
                System.out.println(); // salta de línea por cada fila

                Hecho hecho = new Hecho();
                hecho.setTitulo(titulo);
                hecho.setDescripcion(descripcion);
                hecho.setCategoria(categoria);
                hecho.setPais(pais);
                hecho.setFechaAcontecimiento(fechaAcontecimiento);

                Globales.hechosTotales.add(hecho);
            }



    } catch (IOException e) {
            e.printStackTrace();
        }

        return listaHechos;
    }
}
