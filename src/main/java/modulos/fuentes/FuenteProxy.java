package modulos.fuentes;

import modulos.agregacion.entities.Coleccion;
import modulos.agregacion.entities.filtros.*;
import modulos.shared.Hecho;
import modulos.agregacion.entities.DatosColeccion;
import modulos.shared.utils.FechaParser;
import modulos.shared.utils.Geocodificador;
import org.json.JSONArray;
import org.json.JSONObject;
import modulos.shared.dtos.input.FiltroHechosDTO;
import modulos.shared.dtos.input.SolicitudHechoEliminarInputDTO;
import modulos.buscadores.BuscadorCategoria;
import modulos.buscadores.BuscadorPais;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FuenteProxy {

    private String url_base = "https://api-ddsi.disilab.ar/public/api";
    private String access_token;

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

        public List<Hecho> getHechos(List<Hecho> hechosTotalesProxy, List<Hecho> hechosTotalesDinamica, List<Hecho> hechosTotalesEstatica) {

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
                        hecho.setCategoria(BuscadorCategoria.buscarOCrear(hechosTotalesDinamica, obj.getString("categoria"), hechosTotalesProxy, hechosTotalesEstatica));
                        String pais = Geocodificador.obtenerPais(obj.getDouble("latitud"),obj.getDouble("longitud"));
                        hecho.setPais(BuscadorPais.buscarOCrear(hechosTotalesDinamica, pais, hechosTotalesProxy, hechosTotalesEstatica));
                        hecho.setFechaAcontecimiento(FechaParser.parsearFecha(obj.getString("fecha_hecho")));
                        hecho.setFechaDeCarga(FechaParser.parsearFecha(obj.getString("created_at")));
                        hecho.setFechaUltimaActualizacion(FechaParser.parsearFecha(obj.getString("updated_at")));

                        hechos.add(hecho);
                    }

                    System.out.println("Consulta exitosa, token: " + access_token);

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

    public Hecho getHechoPorId(int id, List<Hecho> hechosTotalesProxy, List<Hecho> hechosTotalesDinamica, List<Hecho> hechosTotalesEstatica) {
        try {
            String urlStr = this.url_base + "/desastres-naturales/?id=" + id;
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
                hecho.setCategoria(BuscadorCategoria.buscarOCrear(hechosTotalesDinamica, obj.getString("categoria"), hechosTotalesProxy, hechosTotalesEstatica));
                String pais = Geocodificador.obtenerPais(obj.getDouble("latitud"),obj.getDouble("longitud"));
                hecho.setPais(BuscadorPais.buscarOCrear(hechosTotalesDinamica, pais, hechosTotalesProxy, hechosTotalesEstatica));
                hecho.setFechaAcontecimiento(FechaParser.parsearFecha(obj.getString("fecha_hecho")));
                hecho.setFechaDeCarga(FechaParser.parsearFecha(obj.getString("created_at")));
                hecho.setFechaUltimaActualizacion(FechaParser.parsearFecha(obj.getString("updated_at")));

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

    public List<Hecho> getHechosMetaMapa(String url_1, FiltroHechosDTO filtros, List<Hecho> hechosTotalesDinamica, List<Hecho> hechosTotalesProxy, List<Hecho> hechosTotalesEstatica){

        try {
            String urlStr = url_1 + "/visualizar/hechos";
            URL url = new URL(urlStr);
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestMethod("GET");

            conexion.setRequestProperty("Content-Type", "application/json");

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("categoria", filtros.getCategoria());
            jsonBody.put("fecha_reporte_desde",filtros.getFechaCargaInicial());
            jsonBody.put("fecha_reporte_hasta", filtros.getFechaCargaFinal());
            jsonBody.put("fecha_acontecimiento_desde", filtros.getFechaAcontecimientoInicial());
            jsonBody.put("fecha_acontecimiento_hasta", filtros.getFechaAcontecimientoFinal());
            jsonBody.put("ubicacion", filtros.getPais());
            /*
            * @RequestParam(required = false) String categoria,
            @RequestParam(required = false, name = "fecha_reporte_desde") String fechaReporteDesde,
            @RequestParam(required = false, name = "fecha_reporte_hasta") String fechaReporteHasta,
            @RequestParam(required = false, name = "fecha_acontecimiento_desde") String fechaAcontecimientoDesde,
            @RequestParam(required = false, name = "fecha_acontecimiento_hasta") String fechaAcontecimientoHasta,
            @RequestParam(required = false) String ubicacion
            * */

            try(OutputStream os = conexion.getOutputStream()){
                byte[] input = jsonBody.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int status = conexion.getResponseCode();

            if (status == 200) {
                String responseBody = new Scanner(conexion.getInputStream()).useDelimiter("\\A").next();

                JSONArray array = new JSONArray(responseBody);

                List<Hecho> hechos = new ArrayList<>();

                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);

                    // Asignar datos a un nuevo objeto Hecho
                    Hecho hecho = new Hecho();
                    hecho.setId(obj.getLong("id"));
                    hecho.setTitulo(obj.getString("titulo"));
                    hecho.setDescripcion(obj.getString("descripcion"));
                    hecho.setCategoria(BuscadorCategoria.buscarOCrear(hechosTotalesDinamica, obj.getString("categoria"), hechosTotalesProxy, hechosTotalesEstatica));
                    hecho.setPais(BuscadorPais.buscarOCrear(hechosTotalesDinamica, obj.getString("pais"), hechosTotalesProxy, hechosTotalesEstatica));
                    hecho.setFechaAcontecimiento(FechaParser.parsearFecha(obj.getString("fechaAcontecimiento")));

                    hechos.add(hecho);

                    return hechos;
                }

            } else {
                System.out.println("Error al consultar con código: " + status);
                String errorMsg = new Scanner(conexion.getErrorStream()).useDelimiter("\\A").next();
                System.out.println("Mensaje: " + errorMsg);
            }

        } catch (Exception e) {
            System.out.println("Excepción al consultar hecho por ID: " + e.getMessage());
        }

        return null;
    }

    public List<Coleccion> getColeccionesMetaMapa(String url_1, List<Hecho> hechosTotalesDinamica, List<Hecho> hechosTotalesProxy, List<Hecho> hechosTotalesEstatica){
        try {

        String urlStr = this.url_base + "/colecciones";
        URL url = new URL(urlStr);
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        conexion.setRequestMethod("GET");

        conexion.setRequestProperty("Content-Type", "application/json");

        int status = conexion.getResponseCode();

            if (status == 200) {



                String responseBody = new Scanner(conexion.getInputStream()).useDelimiter("\\A").next();

                JSONArray array = new JSONArray(responseBody);

                List<Coleccion> colecciones = new ArrayList<>();

                for (int i = 0; i < array.length(); i++) {
                    List<Filtro> filtrosColeccion = new ArrayList<>();
                    JSONObject obj = array.getJSONObject(i);
                    DatosColeccion datosColeccion = new DatosColeccion(obj.getString("titulo"), obj.getString("descripcion"));
                    Coleccion coleccion = new Coleccion(datosColeccion, obj.getLong("id"));
                    coleccion.setTitulo(obj.getString("titulo"));
                    coleccion.setDescripcion(obj.getString("descripcion"));
                    JSONObject filtrosJson = obj.getJSONObject("filtros");
                    ObjectMapper mapper = new ObjectMapper();
                    FiltroHechosDTO filtros = mapper.readValue(filtrosJson.toString(), FiltroHechosDTO.class);

                    FiltroCategoria filtroCategoria = new FiltroCategoria(BuscadorCategoria.buscar(hechosTotalesDinamica, filtros.getCategoria(), hechosTotalesProxy, hechosTotalesEstatica));
                    filtrosColeccion.add(filtroCategoria);
                    FiltroPais filtroPais = new FiltroPais(BuscadorPais.buscar(hechosTotalesDinamica, filtros.getPais(), hechosTotalesProxy, hechosTotalesEstatica));
                    filtrosColeccion.add(filtroPais);
                    FiltroFechaCarga filtroFechaCarga = new FiltroFechaCarga(FechaParser.parsearFecha(filtros.getFechaCargaInicial()),FechaParser.parsearFecha(filtros.getFechaCargaFinal()));
                    filtrosColeccion.add(filtroFechaCarga);
                    FiltroFechaAcontecimiento filtroFechaAcontecimiento = new FiltroFechaAcontecimiento(FechaParser.parsearFecha(filtros.getFechaAcontecimientoInicial()),FechaParser.parsearFecha(filtros.getFechaAcontecimientoFinal()));
                    filtrosColeccion.add(filtroFechaAcontecimiento);

                    List<Hecho> hechos = this.getHechosDeColeccionMetaMapa(url_1, coleccion.getId(), hechosTotalesDinamica, hechosTotalesProxy, hechosTotalesEstatica);

                    coleccion.setCriterio(filtrosColeccion);
                    coleccion.setHechos(hechos);
                    colecciones.add(coleccion);
                }
                return colecciones;

            } else {
                System.out.println("Error al consultar con código: " + status);
                String errorMsg = new Scanner(conexion.getErrorStream()).useDelimiter("\\A").next();
                System.out.println("Mensaje: " + errorMsg);
            }

    } catch (Exception e) {
        System.out.println("Excepción al consultar hecho por ID: " + e.getMessage());
    }

        return null;

    }



///colecciones/{identificador}/hechos
    public List<Hecho> getHechosDeColeccionMetaMapa(String url_1, Long id, List<Hecho> hechosTotalesDinamica, List<Hecho> hechosTotalesProxy, List<Hecho> hechosTotalesEstatica){
        try {

            String urlStr = this.url_base + "/colecciones/?id=" + id + "/hechos";
            URL url = new URL(urlStr);
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestMethod("GET");

            conexion.setRequestProperty("Content-Type", "application/json");

            int status = conexion.getResponseCode();

            if (status == 200) {
                String responseBody = new Scanner(conexion.getInputStream()).useDelimiter("\\A").next();

                JSONArray array = new JSONArray(responseBody);

                List<Coleccion> coleccion = new ArrayList<>();

                List<Hecho> hechos = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);

                    Hecho hecho = new Hecho();
                    hecho.setId(obj.getLong("id"));
                    hecho.setTitulo(obj.getString("titulo"));
                    hecho.setDescripcion(obj.getString("descripcion"));
                    hecho.setCategoria(BuscadorCategoria.buscarOCrear(hechosTotalesDinamica, obj.getString("categoria"), hechosTotalesProxy, hechosTotalesEstatica));
                    hecho.setPais(BuscadorPais.buscarOCrear(hechosTotalesDinamica, obj.getString("pais"), hechosTotalesProxy, hechosTotalesEstatica));
                    hecho.setFechaAcontecimiento(FechaParser.parsearFecha(obj.getString("fechaAcontecimiento")));

                    hechos.add(hecho);

                    return hechos;
                }

            } else {
                System.out.println("Error al consultar con código: " + status);
                String errorMsg = new Scanner(conexion.getErrorStream()).useDelimiter("\\A").next();
                System.out.println("Mensaje: " + errorMsg);
            }

        } catch (Exception e) {
            System.out.println("Excepción al consultar hechos de coleccion: " + e.getMessage());
        }

        return null;

    }

    public void enviarReporte(String url_1, Long id_hecho, String motivo){
        try {

            String urlStr = url_1 + "/reportar";
            URL url = new URL(urlStr);
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestMethod("POST");

            conexion.setRequestProperty("Content-Type", "application/json");

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("id_hecho", id_hecho);
            jsonBody.put("motivo", motivo);

            int status = conexion.getResponseCode();

            if (status == 200) {
                System.out.println("Solicitud enviada con exito");
            }

            else {
                System.out.println("Error al enviar solicitud con código: " + status);
                String errorMsg = new Scanner(conexion.getErrorStream()).useDelimiter("\\A").next();
                System.out.println("Mensaje: " + errorMsg);
            }

        } catch (Exception e) {
            System.out.println("Excepción al enviar solicitud: " + e.getMessage());
        }

    }

    public void enviarSolicitudEliminacionMetaMapa(String url_1, SolicitudHechoEliminarInputDTO data){

        try {

            String urlStr = url_1 + "/solicitud/eliminar-hecho";
            URL url = new URL(urlStr);
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestMethod("POST");

            conexion.setRequestProperty("Content-Type", "application/json");

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("id_usuario", data.getId_usuario());
            jsonBody.put("id_hecho",data.getId_hecho());
            jsonBody.put("justificacion", data.getJustificacion());

            int status = conexion.getResponseCode();

            if (status == 200) {
                System.out.println("Solicitud enviada con exito");
            }

            else {
                System.out.println("Error al enviar solicitud con código: " + status);
                String errorMsg = new Scanner(conexion.getErrorStream()).useDelimiter("\\A").next();
                System.out.println("Mensaje: " + errorMsg);
            }

        } catch (Exception e) {
            System.out.println("Excepción al enviar solicitud: " + e.getMessage());
        }

    }


}








