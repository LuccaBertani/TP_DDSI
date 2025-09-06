package modulos.agregacion.entities.fuentes;

import modulos.agregacion.entities.*;
import modulos.agregacion.entities.filtros.*;
import modulos.buscadores.BuscadorProvincia;
import modulos.shared.dtos.input.CriteriosColeccionDTO;
import modulos.shared.dtos.input.GetHechosColeccionInputDTO;
import modulos.shared.utils.FechaParser;
import modulos.shared.utils.Geocodificador;
import org.json.JSONArray;
import org.json.JSONObject;
import modulos.shared.dtos.input.FiltroHechosDTO;
import modulos.shared.dtos.input.SolicitudHechoEliminarInputDTO;
import modulos.buscadores.BuscadorCategoria;
import modulos.buscadores.BuscadorPais;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FuenteProxy {

    private final String url_base = "https://api-ddsi.disilab.ar/public/api";
    private String access_token;

    public Boolean login(String email, String contrasenia){
        try{
            String urlStr = url_base + "/login";
            URL url = new URL(urlStr);
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestMethod("POST");
            conexion.setRequestProperty("Content-Type", "application/json");
            conexion.setDoOutput(true);

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("email", email);
            jsonBody.put("password", contrasenia);

            try(OutputStream os = conexion.getOutputStream()){
                byte[] input = jsonBody.toString().getBytes(StandardCharsets.UTF_8);
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

        public List<Hecho> getHechos(BuscadorCategoria buscadorCategoria, BuscadorProvincia buscadorProvincia, BuscadorPais buscadorPais) {

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
                        HechoProxy hecho = new HechoProxy();
                        hecho.getAtributosHecho().setTitulo(obj.getString("titulo"));
                        hecho.getAtributosHecho().setDescripcion(obj.getString("descripcion"));
                        hecho.getAtributosHecho().setCategoria(buscadorCategoria.buscarOCrear(obj.getString("categoria")));
                        UbicacionString ubicacionString = Geocodificador.obtenerUbicacion(obj.getDouble("latitud"),obj.getDouble("longitud"));
                        if(ubicacionString != null) {
                            hecho.getAtributosHecho().getUbicacion().setPais(buscadorPais.buscarOCrear(ubicacionString.getPais()));
                            hecho.getAtributosHecho().getUbicacion().setProvincia(buscadorProvincia.buscarOCrear(ubicacionString.getProvincia()));
                        }
                        hecho.getAtributosHecho().setFechaAcontecimiento(FechaParser.parsearFecha(obj.getString("fecha_hecho")));
                        hecho.getAtributosHecho().setFechaCarga(FechaParser.parsearFecha(obj.getString("created_at")));
                        hecho.getAtributosHecho().setFechaUltimaActualizacion(FechaParser.parsearFecha(obj.getString("updated_at")));
                        hecho.getAtributosHecho().setModificado(true);
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

    public Hecho getHechoPorId(int id, BuscadorCategoria buscadorCategoria, BuscadorProvincia buscadorProvincia, BuscadorPais buscadorPais) {
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

                HechoProxy hecho = new HechoProxy();
                hecho.getAtributosHecho().setTitulo(obj.getString("titulo"));
                hecho.getAtributosHecho().setDescripcion(obj.getString("descripcion"));
                hecho.getAtributosHecho().setCategoria(buscadorCategoria.buscarOCrear(obj.getString("categoria")));
                UbicacionString ubicacionString = Geocodificador.obtenerUbicacion(obj.getDouble("latitud"),obj.getDouble("longitud"));
                if(ubicacionString != null) {
                    hecho.getAtributosHecho().getUbicacion().setPais(buscadorPais.buscarOCrear(ubicacionString.getPais()));
                    hecho.getAtributosHecho().getUbicacion().setProvincia(buscadorProvincia.buscarOCrear(ubicacionString.getProvincia()));
                }
                    hecho.getAtributosHecho().setFechaAcontecimiento(FechaParser.parsearFecha(obj.getString("fecha_hecho")));
                    hecho.getAtributosHecho().setFechaCarga(FechaParser.parsearFecha(obj.getString("created_at")));
                    hecho.getAtributosHecho().setFechaUltimaActualizacion(FechaParser.parsearFecha(obj.getString("updated_at")));
                    hecho.getAtributosHecho().setModificado(true);
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





    public List<Coleccion> getColeccionesMetaMapa(String url_1, BuscadorProvincia buscadorProvincia, BuscadorPais buscadorPais, BuscadorCategoria buscadorCategoria){
        try {

        String urlStr = this.url_base + "/get-all";
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
                    JSONObject obj = array.getJSONObject(i);
                    DatosColeccion datosColeccion = new DatosColeccion(obj.getString("titulo"), obj.getString("descripcion"));
                    Coleccion coleccion = new Coleccion(datosColeccion);//TODO ojo con repetir el id de la otra instancia con algun id de una coleccion de esta instancia
                    JSONObject filtrosJson = obj.getJSONObject("criterios");
                    ObjectMapper mapper = new ObjectMapper();
                    CriteriosColeccionDTO filtrosEnString = mapper.readValue(filtrosJson.toString(), CriteriosColeccionDTO.class);

                    FormateadorHecho formateador = new FormateadorHecho();

                    FiltrosColeccion filtros = formateador.formatearFiltrosColeccion(buscadorCategoria, buscadorPais, buscadorProvincia, filtrosEnString);
                    List<Filtro> filtrosLista = formateador.obtenerListaDeFiltros(filtros);

                    coleccion.setCriterios(filtrosLista);
                    GetHechosColeccionInputDTO atributos = new GetHechosColeccionInputDTO();
                    atributos.setId_coleccion(coleccion.getId());
                    atributos.setOrigen(filtrosEnString.getOrigen());
                    atributos.setDescripcion(filtrosEnString.getDescripcion());
                    atributos.setPais(filtrosEnString.getPais());
                    atributos.setCategoria(filtrosEnString.getCategoria());
                    atributos.setContenidoMultimedia(filtrosEnString.getContenidoMultimedia());
                    atributos.setFechaAcontecimientoInicial(filtrosEnString.getFechaAcontecimientoInicial());
                    atributos.setFechaAcontecimientoFinal(filtrosEnString.getFechaAcontecimientoFinal());
                    atributos.setFechaCargaInicial(filtrosEnString.getFechaCargaInicial());
                    atributos.setFechaCargaFinal(filtrosEnString.getFechaCargaFinal());

                    List<Hecho> hechos = this.getHechosDeColeccionMetaMapa(atributos,url_1, buscadorPais, buscadorProvincia, buscadorCategoria);

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


    public List<Hecho> getHechosMetaMapa(String url_1, FiltroHechosDTO filtros, BuscadorCategoria buscadorCategoria, BuscadorPais buscadorPais, BuscadorProvincia buscadorProvincia){

        try {
            String urlStr = url_1 + "/get";
            URL url = new URL(urlStr);
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestMethod("GET");

            conexion.setRequestProperty("Content-Type", "application/json");

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("categoria", filtros.getCriterios().getCategoria());
            jsonBody.put("fecha_reporte_desde",filtros.getCriterios().getFechaCargaInicial());
            jsonBody.put("fecha_reporte_hasta", filtros.getCriterios().getFechaCargaFinal());
            jsonBody.put("fecha_acontecimiento_desde", filtros.getCriterios().getFechaAcontecimientoInicial());
            jsonBody.put("fecha_acontecimiento_hasta", filtros.getCriterios().getFechaAcontecimientoFinal());
            jsonBody.put("ubicacion", filtros.getCriterios().getPais());

            try(OutputStream os = conexion.getOutputStream()){
                byte[] input = jsonBody.toString().getBytes(StandardCharsets.UTF_8);
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
                    HechoProxy hecho = new HechoProxy();
                    hecho.setId(obj.getLong("id"));
                    hecho.getAtributosHecho().setTitulo(obj.getString("titulo"));
                    hecho.getAtributosHecho().setDescripcion(obj.getString("descripcion"));
                    hecho.getAtributosHecho().setCategoria(buscadorCategoria.buscarOCrear(obj.getString("categoria")));
                    UbicacionString ubicacionString = Geocodificador.obtenerUbicacion(obj.getDouble("latitud"),obj.getDouble("longitud"));
                    if(ubicacionString != null) {
                        hecho.getAtributosHecho().getUbicacion().setPais(buscadorPais.buscarOCrear(ubicacionString.getPais()));
                        hecho.getAtributosHecho().getUbicacion().setProvincia(buscadorProvincia.buscarOCrear(ubicacionString.getProvincia()));
                    }
                    hecho.getAtributosHecho().setFechaAcontecimiento(FechaParser.parsearFecha(obj.getString("fechaAcontecimiento")));
                    hecho.getAtributosHecho().setModificado(true);
                    hechos.add(hecho);
                }
                return hechos;
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


    public List<Hecho> getHechosDeColeccionMetaMapa(GetHechosColeccionInputDTO atributos, String url_1, BuscadorPais buscadorPais, BuscadorProvincia buscadorProvincia, BuscadorCategoria buscadorCategoria){
        try {

            String url_concatenada = url_1 + "get/filtrar";
            URL url = new URL(url_concatenada);
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestMethod("POST");
            conexion.setDoOutput(true); // Muy importante para POST

            conexion.setRequestProperty("Content-Type", "application/json");

// Serializa el DTO a JSON
            ObjectMapper mapper = new ObjectMapper();
            String jsonInputString = mapper.writeValueAsString(atributos);

// Escribe el JSON en el body
            try (OutputStream os = conexion.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int status = conexion.getResponseCode();

            if (status == 200) {
                String responseBody = new Scanner(conexion.getInputStream()).useDelimiter("\\A").next();

                JSONArray array = new JSONArray(responseBody);

                List<Hecho> hechos = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);

                    HechoProxy hecho = new HechoProxy();
                    hecho.setId(obj.getLong("id"));
                    hecho.getAtributosHecho().setTitulo(obj.getString("titulo"));
                    hecho.getAtributosHecho().setDescripcion(obj.getString("descripcion"));
                    hecho.getAtributosHecho().setCategoria(buscadorCategoria.buscarOCrear(obj.getString("categoria")));
                    UbicacionString ubicacionString = Geocodificador.obtenerUbicacion(obj.getDouble("latitud"),obj.getDouble("longitud"));
                    if(ubicacionString != null) {
                        hecho.getAtributosHecho().getUbicacion().setPais(buscadorPais.buscarOCrear(ubicacionString.getPais()));
                        hecho.getAtributosHecho().getUbicacion().setProvincia(buscadorProvincia.buscarOCrear(ubicacionString.getProvincia()));
                    }
                    hecho.getAtributosHecho().setFechaAcontecimiento(FechaParser.parsearFecha(obj.getString("fechaAcontecimiento")));
                    hecho.getAtributosHecho().setModificado(true);
                    hechos.add(hecho);

                }
                return hechos;
            } if (status != 200) {
                System.out.println("Error al consultar con código: " + status);
                InputStream errorStream = conexion.getErrorStream();
                if (errorStream != null) {
                    String errorMsg = new Scanner(errorStream).useDelimiter("\\A").next();
                    System.out.println("Mensaje: " + errorMsg);
                } else {
                    System.out.println("No hay mensaje de error disponible.");
                }
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








