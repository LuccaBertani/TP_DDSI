package models.entities;

import java.io.*;
import java.nio.charset.Charset;
import java.text.Normalizer;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import models.repositories.IHechosRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.temporal.WeekFields.ISO;

public class LectorCSV {

    private String dataSet;

    public LectorCSV(String dataSet){
        this.dataSet=dataSet;
    }

    public ModificadorHechos leerCSV(List<Hecho> hechos) {

        List<Hecho> hechosASubir = new ArrayList<>();
        List<Hecho> hechosAModificar = new ArrayList<>();

        try {
            Reader reader = new InputStreamReader(new FileInputStream(this.dataSet), Charset.forName("ISO-8859-1"));

            CSVFormat formato = CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .withDelimiter(';')
                    .withQuote('"'); // Esto es por defecto, pero podés dejarlo explícito
            CSVParser parser = new CSVParser(reader, formato);

            List<String> headers = parser.getHeaderNames();


            // Se cambia el delimitador a una ','
            if(headers.size() == 1){
                parser.close();

                reader = new FileReader(this.dataSet);

                formato = CSVFormat.DEFAULT
                        .withFirstRecordAsHeader()
                        .withDelimiter(',')
                        .withQuote('"'); // Esto es por defecto, pero podés dejarlo explícito
                parser = new CSVParser(reader, formato);

                headers = parser.getHeaderNames();


            }

            System.out.println(headers);
            List<Integer> indicesColumnas = this.filtrarColumnas(headers);
            System.out.println(indicesColumnas);
            //indicesColumnas.forEach(i->i.);

            List<CSVRecord> registrosCSV = parser.getRecords();
            for (int f = 0; f < registrosCSV.size(); f++) {
                CSVRecord fila = registrosCSV.get(f);
                boolean seModificaHecho = false;
                // Campos: [Titulo,Descripcion,Categoria,Latitud,Longitud,FechaDelHecho,Pais]
                // headersDelArchivo: [Titulo,FechaDelAccidente,Auto,Matricula,Pais,Descripcion]
                // indicesColumnas: [0,5,-1,-1,-1,-1,-1] -> filtrarColumnas
                //PrimeraLectura : [Cataratas,10/10/10,reno12,23125412,Argentina,Skibidi]
                // guardo nombre: if(lista.get(0) != -1){
                // hecho.setNombre(registro.get(0));
                // guardo descripcion: if(lista.get(5) != -1){
                // hecho.setDescripcion(registro.get(5))
                // }

                // if(lista.get(1) == 1) {hecho.setDescripcion("N/A")}

                List<String> registros = new ArrayList<>();
                fila.forEach(registros::add);


                Hecho hecho = new Hecho();

                hecho.setOrigen(Origen.DATASET);

                hecho.setTitulo((indicesColumnas.get(0) != -1) ? registros.get(indicesColumnas.get(0)) : "N/A");

                Optional<Hecho> hecho0 = hechos.stream().filter(h->this.normalizarYComparar(h.getTitulo(), hecho.getTitulo())).findFirst();

                if (hecho0.isPresent() && !hecho0.get().getTitulo().equals("N/A")){
                    System.out.println("El hecho está repetido");
                    seModificaHecho = true; // El hecho se sobreescribe cuando se repite el título
                }

                hecho.setDescripcion((indicesColumnas.get(1) != -1) ? registros.get(indicesColumnas.get(1)) : "N/A");

                String categoriaString = indicesColumnas.get(2) != -1 ? registros.get(indicesColumnas.get(2)) : "N/A";
                Optional<Hecho> hecho1 = hechos.stream().filter(h->this.normalizarYComparar(h.getCategoria().getTitulo(), categoriaString)).findFirst();
                Categoria categoria;
                // Si la categoría no existe, se crea

                if (hecho1.isPresent()){
                    categoria = hecho1.get().getCategoria();
                    hecho.setCategoria(categoria);
                }
                else{
                    categoria = new Categoria();
                    categoria.setTitulo(categoriaString);
                    hecho.setCategoria(categoria);
                }


                String paisString;
                if (indicesColumnas.get(3) != -1 && indicesColumnas.get(4) != -1 &&
                        (!registros.get(indicesColumnas.get(3)).equals("") && !registros.get(indicesColumnas.get(4)).equals(""))){
                    Double latitud = Double.parseDouble(registros.get(indicesColumnas.get(3)));
                    Double longitud = Double.parseDouble(registros.get(indicesColumnas.get(4)));
                    paisString = Geocodificador.obtenerPais(latitud, longitud);
                }

                else {
                    paisString = indicesColumnas.get(6) != -1 ? registros.get(indicesColumnas.get(6)) : "N/A";
                }

                Optional<Hecho> hecho2 = hechos.stream().filter(h->this.normalizarYComparar(h.getPais().getPais(), paisString)).findFirst();
                Pais pais;
                // Si el país no existe, se crea

                if (hecho2.isPresent() && !hecho2.get().getPais().getPais().equals("N/A")){
                    pais = hecho2.get().getPais();
                    hecho.setPais(pais);

                } else if (hecho2.isEmpty()){
                    pais = new Pais();
                    pais.setPais(paisString);
                    hecho.setPais(pais);
                }
                //ZonedDateTime fecha = FechaParser.parsearFecha(registros.get(indicesColumnas.get(5)));
                //ZonedDateTime fecha = (indicesColumnas.get(5) != -1) ? fecha :ZonedDateTime.parse(registros.get(indicesColumnas.get(5)));
                hecho.setFechaAcontecimiento((indicesColumnas.get(5) != -1) ? FechaParser.parsearFecha(registros.get(indicesColumnas.get(5))) : ZonedDateTime.of(0, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")));

                hecho.setFechaDeCarga(ZonedDateTime.now());

                if (seModificaHecho){
                    hechosAModificar.add(hecho);
                }
                else{
                    hechosASubir.add(hecho);
                }

                System.out.println("Hechos a modificar: ");
                for (Hecho hechoASubir : hechosAModificar){
                    System.out.println(hechoASubir.getTitulo());
                    System.out.println(hechoASubir.getDescripcion());
                    System.out.println(hechoASubir.getCategoria().getTitulo());
                    System.out.println(hechoASubir.getPais().getPais());
                    System.out.println(hechoASubir.getFechaAcontecimiento());
                    System.out.println(hechoASubir.getFechaDeCarga());
                }


                System.out.println("Hechos a subir: ");
                for (Hecho hechoASubir : hechosASubir){
                    System.out.println(hechoASubir.getTitulo());
                    System.out.println(hechoASubir.getDescripcion());
                    System.out.println(hechoASubir.getCategoria().getTitulo());
                    System.out.println(hechoASubir.getPais().getPais());
                    System.out.println(hechoASubir.getFechaAcontecimiento());
                    System.out.println(hechoASubir.getFechaDeCarga());
                }
            }

            parser.close();
        }
        catch(IOException e){
            throw new RuntimeException("Error al leer el archivo CSV: " + e.getMessage(), e);
        }

        return new ModificadorHechos(hechosASubir, hechosAModificar);
    }

    // Analizo qué columnas me interesan. Solo leo esas despues.
    private List<Integer> filtrarColumnas(List<String> headers) {

        // Campos: [Titulo,Descripcion,Categoria,Latitud,Longitud,FechaDelHecho]
        // headersDelArchivo: [Titulo,FechaDelAccidente,Auto,Matricula,Pais,Descripcion]
        // lista: [0,5,-1,-1,-1,-1]

        List<Integer> indicesColumnas = new ArrayList<>(Collections.nCopies(Globales.campos.size(), -1));


        // Mapa de sinónimos: cada clave es un patrón, el valor es el campo real que representa
        /*Map<String, String> sinonimos = Map.of(
                "titulo_hecho", "titulo",
                "id_hecho", "titulo",
                "evento", "titulo",
                "fecha", "fechadelhecho"
        );*/

        for (int i = 0; i < headers.size(); i++) {
            String valorColumna = headers.get(i);
            valorColumna = this.normalizar(valorColumna);
            int j = 0;
            for (String campoEsperado : Globales.campos) {
                if (valorColumna.equals(campoEsperado)) {
                    indicesColumnas.set(j, i); // i: posicion del campo del header. j: posicion de la lista
                }
                j++;
            }

        }
        return indicesColumnas;
    }


    private String normalizar(String texto) {
        // Quitar tildes
        String sinTildes = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        // Quitar espacios y pasar a minúsculas
        return sinTildes.replaceAll("\\s+", "").toLowerCase();
    }

    private Boolean normalizarYComparar(String s1, String s2){
        s1=normalizar(s1);
        s2=normalizar(s2);

        return s1.equals(s2);
    }

}
