package models.entities.fuentes;

import lombok.Getter;
import lombok.Setter;
import models.entities.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class FuenteEstatica implements Fuente {
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

                Optional<Hecho> hecho0 = Globales.hechosTotales.stream().filter(h->h.getTitulo().equals(titulo)).findFirst();

                if (!hecho0.isEmpty()){
                    Globales.hechosTotales.remove(hecho0); // El hecho se sobreescribe cuando se repite el título
                }

                //TODO
                Optional<Hecho> hecho1 = Globales.hechosTotales.stream().filter(h-> h.getCategoria().getTitulo().toLowerCase().equals(categoriaString.toLowerCase())).findFirst();

                Categoria categoria;
                if (hecho1.isEmpty()){
                    categoria = new Categoria();
                    categoria.setTitulo(categoriaString);
                }
                else{
                    categoria = hecho1.get().getCategoria();
                }

                //TODO
                Optional<Hecho> hecho2 = Globales.hechosTotales.stream().filter(h-> h.getPais().getPais().toLowerCase().equals(nombrePais.toLowerCase())).findFirst();
                Pais pais;
                if (hecho2.isEmpty()){
                    pais = new Pais();
                    pais.setPais(nombrePais);
                }
                else{
                    pais = hecho2.get().getPais();
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
