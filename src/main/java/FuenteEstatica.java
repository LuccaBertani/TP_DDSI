import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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
                LocalDate fechaAcontecimiento = LocalDate.parse(valores[5],formatter);
                for (String valor : valores) {
                    System.out.print(valor + " | ");
                }
                System.out.println(); // salta de línea por cada fila
            }



    } catch (IOException e) {
            e.printStackTrace();
        }

        return listaHechos;
    }
}
