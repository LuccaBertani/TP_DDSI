package raiz.models.entities.fuentes;

import org.json.JSONArray;
import org.json.JSONObject;
import raiz.models.entities.FechaParser;
import raiz.models.entities.Geocodificador;
import raiz.models.entities.Hecho;
import raiz.models.entities.ModificadorHechos;
import raiz.models.entities.buscadores.BuscadorCategoria;
import raiz.models.entities.buscadores.BuscadorPais;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FuenteProxy implements Fuente {
    // TODO en entrega 2
    private String url_base;
    private String access_token;

    public FuenteProxy(String url_base){
        this.url_base = url_base;
    }

    public Boolean login(String email, String contraseña){
        try{
            String urlStr = url_base + "/login";
            URL url = new URL(urlStr);
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestMethod("POST");
            conexion.setRequestProperty("Content-Type", "application/json");
            conexion.setDoOutput(true);

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("email", email);
            jsonBody.put("password", contraseña);

            try(OutputStream os = conexion.getOutputStream()){
                byte[] input = jsonBody.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int status = conexion.getResponseCode();

            if (status == 200){
                // Leer respuesta JSON
                String responseBody = new Scanner(conexion.getInputStream()).useDelimiter("\\A").next();
                JSONObject jsonResponse = new JSONObject(responseBody);
                access_token = jsonResponse.getString("token");
                System.out.println("Login exitoso, token: " + access_token);
                return true;
            } else {
                System.out.println("Login fallido con código: " + status);
                String errorMsg = new Scanner(conexion.getErrorStream()).useDelimiter("\\A").next();
                System.out.println("Mensaje de error: " + errorMsg);
                return false;
            }

        } catch (Exception e){
            System.out.println("Error en login: " + e.getMessage());
            return false;
        }

        }

        public List<Hecho> getHechos(List<Hecho> hechosTotales) {

            List<Hecho> hechos = new ArrayList<>();

            try {
                String urlStr = this.url_base + "/desastres";
                URL url = new URL(urlStr);
                HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
                conexion.setRequestMethod("GET");
                conexion.setRequestProperty("Authorization", "Bearer " + this.access_token);
                conexion.setRequestProperty("Content-Type", "application/json");

                int status = conexion.getResponseCode();

                if (status == 200) {
                    // Leer respuesta JSON
                    String responseBody = new Scanner(conexion.getInputStream()).useDelimiter("\\A").next();

                    JSONArray array = new JSONArray(responseBody);

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);

                        // Asignar datos a un nuevo objeto Hecho
                        Hecho hecho = new Hecho();
                        hecho.setTitulo(obj.getString("titulo"));
                        hecho.setDescripcion(obj.getString("descripcion"));
                        hecho.setCategoria(BuscadorCategoria.buscarOCrear(hechosTotales, obj.getString("categoria")));
                        String pais = Geocodificador.obtenerPais(obj.getDouble("latitud"),obj.getDouble("longitud"));
                        hecho.setPais(BuscadorPais.buscarOCrear(hechosTotales,pais));
                        hecho.setFechaAcontecimiento(FechaParser.parsearFecha(obj.getString("fecha_hecho")));
                        hecho.setFechaDeCarga(FechaParser.parsearFecha(obj.getString("created_at")));

                        hechos.add(hecho);
                    }

                    System.out.println("Login exitoso, token: " + access_token);

                } else {
                    System.out.println("Conexion fallida con código: " + status);
                    String errorMsg = new Scanner(conexion.getErrorStream()).useDelimiter("\\A").next();
                    System.out.println("Mensaje de error: " + errorMsg);
                    return null;
                }

            }
            catch (Exception e) {
            System.out.println("Error al obtener hechos: " + e.getMessage());
        }

            return hechos;
    }

    public Hecho getHechoPorId(int id, List<Hecho> hechosTotales) {
        try {
            String urlStr = this.url_base + "/desastres-naturales/" + id;
            URL url = new URL(urlStr);
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestMethod("GET");

            // Autorización con token
            conexion.setRequestProperty("Authorization", "Bearer " + this.access_token);
            conexion.setRequestProperty("Content-Type", "application/json");

            int status = conexion.getResponseCode();

            if (status == 200) {
                String responseBody = new Scanner(conexion.getInputStream()).useDelimiter("\\A").next();
                JSONObject obj = new JSONObject(responseBody);

                Hecho hecho = new Hecho();
                hecho.setTitulo(obj.getString("titulo"));
                hecho.setDescripcion(obj.getString("descripcion"));
                hecho.setCategoria(BuscadorCategoria.buscarOCrear(hechosTotales, obj.getString("categoria")));
                String pais = Geocodificador.obtenerPais(obj.getDouble("latitud"),obj.getDouble("longitud"));
                hecho.setPais(BuscadorPais.buscarOCrear(hechosTotales,pais));
                hecho.setFechaAcontecimiento(FechaParser.parsearFecha(obj.getString("fecha_hecho")));
                hecho.setFechaDeCarga(FechaParser.parsearFecha(obj.getString("created_at")));

                return hecho;

            } else {
                System.out.println("Error al consultar hecho con ID " + id + ". Código: " + status);
                String errorMsg = new Scanner(conexion.getErrorStream()).useDelimiter("\\A").next();
                System.out.println("Mensaje: " + errorMsg);
            }

        } catch (Exception e) {
            System.out.println("Excepción al consultar hecho por ID: " + e.getMessage());
        }

        return null;
    }


    @Override
    public ModificadorHechos leerFuente(List<Hecho> hechos) {
        return null;
    }
}



