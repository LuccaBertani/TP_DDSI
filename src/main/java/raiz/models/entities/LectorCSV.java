package raiz.models.entities;

import java.io.*;
import java.nio.charset.Charset;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import raiz.models.entities.buscadores.BuscadorCategoria;
import raiz.models.entities.buscadores.BuscadorHechoIdentico;
import raiz.models.entities.buscadores.BuscadorPais;

public class LectorCSV {
    private static List<String> campos = new ArrayList<>(Arrays.asList("titulo","descripcion","categoria","latitud","longitud","fechadelhecho","pais"));
    private String dataSet;

    public LectorCSV(String dataSet){
        this.dataSet=dataSet;
    }

    /*public ModificadorHechos leerCSV(List<Hecho> hechosFuenteProxy, List<Hecho> hechosFuenteDinamica, List<Hecho> hechosFuenteEstatica) {

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

                List<String> registros = new ArrayList<>();
                fila.forEach(registros::add);


                Hecho hecho = new Hecho();

                hecho.setOrigen(Origen.FUENTE_ESTATICA);

                hecho.setTitulo((indicesColumnas.get(0) != -1) ? registros.get(indicesColumnas.get(0)) : "N/A");
                //Se leen los de fuente estatica
                Optional<Hecho> hecho0 = hechosFuenteEstatica.stream().filter(h->Normalizador.normalizarYComparar(h.getTitulo(), hecho.getTitulo())).findFirst();

                if (hecho0.isPresent() && !hecho0.get().getTitulo().equals("N/A")){
                    System.out.println("El hecho está repetido");
                    seModificaHecho = true; // El hecho se sobreescribe cuando se repite el título
                }

                hecho.setDescripcion((indicesColumnas.get(1) != -1) ? registros.get(indicesColumnas.get(1)) : "N/A");

                String categoriaString = indicesColumnas.get(2) != -1 ? registros.get(indicesColumnas.get(2)) : "N/A";

                hecho.setCategoria(BuscadorCategoria.buscarOCrear(hechosFuenteDinamica,categoriaString,hechosFuenteProxy,hechosFuenteEstatica));

                String paisString;
                if (indicesColumnas.get(3) != -1 && indicesColumnas.get(4) != -1 &&
                        (!registros.get(indicesColumnas.get(3)).equals("") && !registros.get(indicesColumnas.get(4)).equals(""))) {
                    Double latitud = Double.parseDouble(registros.get(indicesColumnas.get(3)));
                    Double longitud = Double.parseDouble(registros.get(indicesColumnas.get(4)));
                    paisString = Geocodificador.obtenerPais(latitud, longitud);
                }
                else {
                    paisString = indicesColumnas.get(6) != -1 ? registros.get(indicesColumnas.get(6)) : "N/A";
                }

                hecho.setPais(BuscadorPais.buscarOCrear(hechosFuenteDinamica,paisString,hechosFuenteProxy,hechosFuenteEstatica));

                //ZonedDateTime fecha = FechaParser.parsearFecha(registros.get(indicesColumnas.get(5)));
                //ZonedDateTime fecha = (indicesColumnas.get(5) != -1) ? fecha :ZonedDateTime.parse(registros.get(indicesColumnas.get(5)));
                hecho.setFechaAcontecimiento((indicesColumnas.get(5) != -1) ? FechaParser.parsearFecha(registros.get(indicesColumnas.get(5))) : ZonedDateTime.of(0, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")));

                hecho.setFechaDeCarga(ZonedDateTime.now());
                hecho.setFechaUltimaActualizacion(hecho.getFechaDeCarga());
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
    }*/
    // Entrega 3: los hechos no se pisan los atributos

    public List<Hecho> leerCSV(List<Hecho> hechosFuenteProxy, List<Hecho> hechosFuenteDinamica, List<Hecho> hechosFuenteEstatica, List<String> datasets) {

        List<Hecho> hechosASubir = new ArrayList<>();

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

                List<String> registros = new ArrayList<>();
                fila.forEach(registros::add);


                Hecho hecho = new Hecho();

                hecho.setOrigen(Origen.FUENTE_ESTATICA);

                boolean tituloRepetido = false;

                hecho.setTitulo((indicesColumnas.get(0) != -1) ? registros.get(indicesColumnas.get(0)) : "N/A");
                //Se leen los de fuente estatica
                Optional<Hecho> hecho0 = hechosFuenteEstatica.stream().filter(h->Normalizador.normalizarYComparar(h.getTitulo(), hecho.getTitulo())).findFirst();

                if (hecho0.isPresent() && !hecho0.get().getTitulo().equals("N/A")){
                    System.out.println("El hecho está repetido");
                    tituloRepetido = true;
                }

                hecho.setDescripcion((indicesColumnas.get(1) != -1) ? registros.get(indicesColumnas.get(1)) : "N/A");

                String categoriaString = indicesColumnas.get(2) != -1 ? registros.get(indicesColumnas.get(2)) : "N/A";

                hecho.setCategoria(BuscadorCategoria.buscarOCrear(hechosFuenteDinamica,categoriaString,hechosFuenteProxy,hechosFuenteEstatica));

                String paisString;
                if (indicesColumnas.get(3) != -1 && indicesColumnas.get(4) != -1 &&
                        (!registros.get(indicesColumnas.get(3)).equals("") && !registros.get(indicesColumnas.get(4)).equals(""))) {
                    Double latitud = Double.parseDouble(registros.get(indicesColumnas.get(3)));
                    Double longitud = Double.parseDouble(registros.get(indicesColumnas.get(4)));
                    paisString = Geocodificador.obtenerPais(latitud, longitud);
                }
                else {
                    paisString = indicesColumnas.get(6) != -1 ? registros.get(indicesColumnas.get(6)) : "N/A";
                }

                hecho.setPais(BuscadorPais.buscarOCrear(hechosFuenteDinamica,paisString,hechosFuenteProxy,hechosFuenteEstatica));

                //ZonedDateTime fecha = FechaParser.parsearFecha(registros.get(indicesColumnas.get(5)));
                //ZonedDateTime fecha = (indicesColumnas.get(5) != -1) ? fecha :ZonedDateTime.parse(registros.get(indicesColumnas.get(5)));
                hecho.setFechaAcontecimiento((indicesColumnas.get(5) != -1) ? FechaParser.parsearFecha(registros.get(indicesColumnas.get(5))) : ZonedDateTime.of(0, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")));

                hecho.setFechaDeCarga(ZonedDateTime.now());
                hecho.setFechaUltimaActualizacion(hecho.getFechaDeCarga());

                if (tituloRepetido){
                    boolean existeHechoIdentico = BuscadorHechoIdentico.existeHechoIdentico(hecho, hechosFuenteEstatica);
                    if (existeHechoIdentico){
                        hecho0.get().getDataSets().add(this.dataSet);
                        continue; // Evito agregar un hecho identico
                    }
                    else{
                        hecho.getDataSets().add(this.dataSet);
                    }
                }

                hechosASubir.add(hecho);

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
            datasets.add(this.dataSet);
            parser.close();
        }
        catch(IOException e){
            throw new RuntimeException("Error al leer el archivo CSV: " + e.getMessage(), e);
        }

        return hechosASubir;
    }


    // Analizo qué columnas me interesan. Solo leo esas después.
    private List<Integer> filtrarColumnas(List<String> headers) {


        List<Integer> indicesColumnas = new ArrayList<>(Collections.nCopies(LectorCSV.campos.size(), -1));


        // Mapa de sinónimos: cada clave es un patrón, el valor es el campo real que representa
        /*Map<String, String> sinonimos = Map.of(
                "titulo_hecho", "titulo",
                "id_hecho", "titulo",
                "evento", "titulo",
                "fecha", "fechadelhecho"
        );*/

        for (int i = 0; i < headers.size(); i++) {
            String valorColumna = headers.get(i);
            valorColumna = Normalizador.normalizar(valorColumna);
            int j = 0;
            for (String campoEsperado : LectorCSV.campos) {
                if (valorColumna.equals(campoEsperado)) {
                    indicesColumnas.set(j, i); // i: posicion del campo del header. j: posicion de la lista
                }
                j++;
            }

        }
        return indicesColumnas;
    }



}
