import lombok.Getter;
import lombok.Setter;

import javax.swing.text.html.Option;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
        DateTimeFormatter formatter =  DateTimeFormatter.ISO_ZONED_DATE_TIME;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(this.dataSet), Charset.forName("ISO-8859-1"))))
        {

            linea = br.readLine(); // Me salteo la primera fila
            while ((linea = br.readLine()) != null) {
                String[] valores = linea.split(";");

                String titulo = valores[0];
                String descripcion = valores[1];
                String categoriaString = valores[2];

                Double latitud = Double.parseDouble(valores[3]);
                Double longitud = Double.parseDouble(valores[4]);

                String nombrePais = Geocodificador.obtenerPais(latitud, longitud);

                Optional<Hecho> hecho0 = Globales.hechosTotales.stream().filter(h->h.getCategoria().getTitulo().toLowerCase()
                        == categoriaString.toLowerCase()).findFirst();

                Categoria categoria;
                if (hecho0.isEmpty()){
                    categoria = new Categoria();
                    categoria.setTitulo(categoriaString);
                }
                else{
                    categoria = hecho0.get().getCategoria();
                }

                Optional<Hecho> hecho1 = Globales.hechosTotales.stream().filter(h->h.getPais().getPais().toLowerCase()
                        == nombrePais.toLowerCase()).findFirst();
                Pais pais;
                if (hecho1.isEmpty()){
                    pais = new Pais();
                    pais.setPais(nombrePais);
                }
                else{
                    pais = hecho1.get().getPais();
                }

                ZonedDateTime fechaAcontecimiento = ZonedDateTime.parse(valores[5],formatter);
                ZonedDateTime fechaCarga = ZonedDateTime.now();

                Hecho nuevoHecho = new Hecho();
                nuevoHecho.setTitulo(titulo);
                nuevoHecho.setDescripcion(descripcion);
                nuevoHecho.setCategoria(categoria);
                nuevoHecho.setPais(pais);
                nuevoHecho.setFechaAcontecimiento(fechaAcontecimiento);
                nuevoHecho.setFechaDeCarga(fechaCarga);
                nuevoHecho.setOrigen(Origen.DATASET);
                listaHechos.add(nuevoHecho);
                Globales.hechosTotales.add(nuevoHecho);
            }



    } catch (IOException e) {
            e.printStackTrace();
        }

        return listaHechos;
    }
}
