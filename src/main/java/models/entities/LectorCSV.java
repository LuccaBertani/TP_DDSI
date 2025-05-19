package models.entities;

import java.io.*;
import java.nio.charset.Charset;
import java.text.Normalizer;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class LectorCSV {

    private String dataSet;

    public LectorCSV(String dataSet){
        this.dataSet=dataSet;
    }

    public ModificadorHechos leerCSV(List<Hecho> hechos){

        File archivo = new File(this.dataSet);

        if (!archivo.exists() || !archivo.canRead()) {
            throw new SecurityException("No se puede acceder al archivo CSV: " + this.dataSet);
        }

        // Los cambios en la lista de hechos actuales tienen que verse reflejados en el repo de hechos
        // Hago una lista de los hechos a subir, y una lista de hechos a modificar
        List<Hecho> hechosASubir = new ArrayList<>();
        List<Hecho> hechosAModificar = new ArrayList<>();

        String linea;
        DateTimeFormatter formatter =  DateTimeFormatter.ISO_ZONED_DATE_TIME;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(this.dataSet), Charset.forName("ISO-8859-1"))))
        {

            linea = br.readLine();
            List<String> valoresColumnas = List.of(linea.split(";"));
            Map<String, Integer> mapeoValorXIndice = this.filtrarColumnas(valoresColumnas);


            while ((linea = br.readLine()) != null) {
                Boolean seModificaHecho = false;
                String[] valores = linea.split(";");
                String titulo;

                if (mapeoValorXIndice.containsKey("titulo")){
                    titulo = valores[mapeoValorXIndice.get("titulo")];
                }

                // TODO así con el resto


                /*String nombrePais = Geocodificador.obtenerPais(latitud, longitud);

                Optional<Hecho> hecho0 = hechos.stream().filter(h->h.getTitulo().equals(titulo)).findFirst();

                if (!hecho0.isEmpty()){
                    seModificaHecho = true; // El hecho se sobreescribe cuando se repite el título
                }

                Optional<Hecho> hecho1 = hechos.stream().filter(h-> h.getCategoria().getTitulo().toLowerCase().equals(categoriaString.toLowerCase())).findFirst();
                Categoria categoria;

                // Si la categoría no existe, se crea
                if (hecho1.isEmpty()){
                    categoria = new Categoria();
                    categoria.setTitulo(categoriaString);
                }
                else{
                    categoria = hecho1.get().getCategoria();
                }

                Optional<Hecho> hecho2 = Globales.hechosTotales.stream().filter(h-> h.getPais().getPais().toLowerCase().equals(nombrePais.toLowerCase())).findFirst();
                Pais pais;

                // Si el país no existe, se crea
                if (hecho2.isEmpty()){
                    pais = new Pais();
                    pais.setPais(nombrePais);
                }
                else{
                    pais = hecho2.get().getPais();
                }

                ZonedDateTime fechaAcontecimiento = ZonedDateTime.parse(valores[5],formatter);
                ZonedDateTime fechaCarga = ZonedDateTime.now();

                Hecho hecho = new Hecho();
                hecho.setTitulo(titulo);
                hecho.setDescripcion(descripcion);
                hecho.setCategoria(categoria);
                hecho.setPais(pais);
                hecho.setFechaAcontecimiento(fechaAcontecimiento);
                hecho.setFechaDeCarga(fechaCarga);
                hecho.setOrigen(Origen.DATASET);

                if (!seModificaHecho){
                    hechosASubir.add(hecho);
                }
                else{
                    hecho.setId(hecho0.get().getId()); // Se mantiene el id
                    hechosAModificar.add(hecho);
                }*/

            }



        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo CSV: " + e.getMessage(), e);
        }

        return new ModificadorHechos(hechosASubir, hechosAModificar);
    }

    // Analizo qué columnas me interesan. Solo leo esas despues.
    private Map<String, Integer> filtrarColumnas(List<String> valoresColumnas){

        List<String>camposEsperados = new ArrayList<>(Arrays.asList("titulo","descripcion","categoria","latitud","longitud","fechadelhecho"));


        Map<String, Integer> mapeoValorXIndice = new HashMap<>();




        for (String valorColumna : valoresColumnas){
            Boolean encontrado = false;
            for (String campoEsperado : camposEsperados){
                if (valorColumna.equals(campoEsperado)){
                    encontrado = true;
                    mapeoValorXIndice.put(campoEsperado, valoresColumnas.indexOf(valorColumna));
                }
            }
            if (!encontrado){
                // Capaz los títulos no se guardan y tienen algún id para identificarlos
                if (valorColumna.equals("id_hecho")){
                    mapeoValorXIndice.put("titulo", valoresColumnas.indexOf(valorColumna));
                }
                // Una columna puede decir fecha en vez de fecha del hecho
                else if (valorColumna.contains("fecha")){
                    mapeoValorXIndice.put("fechadelhecho", valoresColumnas.indexOf(valorColumna));
                }
                // El lugar del hecho puede estar determinado de otras formas
                else if (!mapeoValorXIndice.containsKey("longitud") && (valorColumna.contains("lugar") || valorColumna.contains("pais"))){
                    mapeoValorXIndice.put("pais", valoresColumnas.indexOf(valorColumna));
                }

            }
        }

        return mapeoValorXIndice;


    }



    private String normalizar(String texto) {
        // Quitar tildes
        String sinTildes = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        // Quitar espacios y pasar a minúsculas
        return sinTildes.replaceAll("\\s+", "").toLowerCase();
    }

}
