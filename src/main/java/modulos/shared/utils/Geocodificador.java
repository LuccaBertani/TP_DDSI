package modulos.shared.utils;

import modulos.agregacion.entities.UbicacionString;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Geocodificador {

    public static UbicacionString obtenerUbicacion(Double latitud, Double longitud) {
        try {
            String urlStr = String.format(
                    java.util.Locale.US,
                    "https://nominatim.openstreetmap.org/reverse?format=json&lat=%f&lon=%f",
                    latitud, longitud
            );

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String linea;

            while ((linea = in.readLine()) != null) {
                response.append(linea);
            }

            in.close();

            JSONObject json = new JSONObject(response.toString());
            UbicacionString ubicacion = new UbicacionString();
            ubicacion.setPais(json.getJSONObject("address").getString("country"));
            ubicacion.setProvincia(json.getJSONObject("address").getString("state"));
            return ubicacion;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}