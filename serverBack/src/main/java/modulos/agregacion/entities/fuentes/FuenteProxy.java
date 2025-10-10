package modulos.agregacion.entities.fuentes;

import modulos.agregacion.entities.DbMain.*;
import modulos.agregacion.entities.DbMain.filtros.*;
import modulos.agregacion.entities.DbMain.hechoRef.HechoRef;
import modulos.agregacion.entities.DbProxy.HechoProxy;
import modulos.agregacion.entities.atributosHecho.ContenidoMultimedia;
import modulos.agregacion.entities.atributosHecho.Origen;
import modulos.agregacion.entities.fuentes.Requests.*;
import modulos.shared.utils.FormateadorHecho;
import modulos.buscadores.*;
import modulos.shared.dtos.input.*;
import modulos.shared.utils.FechaParser;
import modulos.shared.utils.Geocodificador;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class FuenteProxy {

    WebClient webClient = WebClient.create("https://api-ddsi.disilab.ar/public/api");
    WebClient webClientMetaMapa = WebClient.create("http://localhost:8080");

    public Mono<String> login(String email, String contrasenia){

            LoginRequest request = new LoginRequest();
            request.setEmail(email);
            request.setPassword(contrasenia);

            // devolvés el token
            return webClient.post()
                    .uri("/login")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, response ->
                            response.bodyToMono(String.class)
                                    .map(body -> new RuntimeException("Error 4xx: " + body))
                                    .flatMap(Mono::error)
                    )
                    .bodyToMono(LoginResponse.class)
                    .map(LoginResponse::getToken);
    }

        public Mono<List<Hecho>> getHechos(BuscadoresRegistry buscadores, String token) {

            List<Hecho> hechos = new ArrayList<>();

                return webClient.get()
                        .uri(uriBuilder -> uriBuilder.path("/desastres")
                                .queryParam("token", token).build())
                        .retrieve()
                        .bodyToMono(HechosResponse.class).map(response -> {
                            for(HechoResponse hechoResponse : response.getHechos()){
                            Hecho hecho = this.setearHecho(hechoResponse, buscadores);
                            hechos.add(hecho);
                        }
                            return hechos;
                        });
    }

    private Hecho setearHecho(HechoResponse hechoResponse, BuscadoresRegistry buscadores) {
        HechoProxy hecho = new HechoProxy();
        hecho.getAtributosHecho().setTitulo(hechoResponse.getTitulo());
        hecho.getAtributosHecho().setDescripcion(hechoResponse.getDescripcion());
        Categoria categoria = buscadores.getBuscadorCategoria().buscar(hechoResponse.getCategoria());
        hecho.getAtributosHecho().setCategoria_id(categoria != null ? categoria.getId() : null);
        UbicacionString ubicacionString = Geocodificador.obtenerUbicacion(hechoResponse.getLatitud(), hechoResponse.getLongitud());
        if(ubicacionString != null) {
            Pais pais = buscadores.getBuscadorPais().buscar(ubicacionString.getPais());
            Provincia provincia = buscadores.getBuscadorProvincia().buscar(ubicacionString.getProvincia());
            Ubicacion ubicacion = buscadores.getBuscadorUbicacion().buscarOCrear(pais, provincia);
            hecho.getAtributosHecho().setUbicacion_id(ubicacion != null ? ubicacion.getId() : null);
            hecho.getAtributosHecho().setLatitud(hechoResponse.getLatitud());
            hecho.getAtributosHecho().setLatitud(hechoResponse.getLongitud());
        }
        hecho.getAtributosHecho().setOrigen(Origen.FUENTE_PROXY_METAMAPA);
        hecho.getAtributosHecho().setFuente(Fuente.PROXY);
        hecho.getAtributosHecho().setFechaAcontecimiento(FechaParser.parsearFecha(hechoResponse.getFecha_hecho()));
        hecho.getAtributosHecho().setFechaCarga(FechaParser.parsearFecha(hechoResponse.getCreated_at()));
        hecho.getAtributosHecho().setFechaUltimaActualizacion(FechaParser.parsearFecha(hechoResponse.getUpdated_at()));
        hecho.getAtributosHecho().setModificado(true);
        return hecho;
    }

    public Mono<Hecho> getHechoPorId(Long id, BuscadoresRegistry buscadores, String token) {

            return webClient.get()
                    .uri(UriBuilder -> UriBuilder.path("/desastres-naturales")
                            .queryParam("token",token)
                            .queryParam("id", id)
                            .build())
                            .retrieve()
                            .bodyToMono(HechoResponse.class)
                            .map(hechoResponse -> this.setearHecho(hechoResponse, buscadores));
    }

    public Mono<List<Coleccion>> getColeccionesMetaMapa(BuscadoresRegistry buscadores){

        List<Coleccion> colecciones = new ArrayList<>();

        return webClientMetaMapa.get().uri("/get-all")
                .retrieve()
                .bodyToMono(ColeccionesResponse.class)
                .map(coleccionesResponse -> {
                    for(ColeccionResponse coleccionResponse : coleccionesResponse.getColecciones()){
                        Coleccion coleccion = new Coleccion();
                        coleccion.setTitulo(coleccionResponse.getTitulo());
                        coleccion.setDescripcion(coleccionResponse.getDescripcion());
                        coleccion.setActivo(true);
                        coleccion.setModificado(true);
                        // TODO lucca deforme
                        //coleccion.setCriterios(FormateadorHecho.obtenerListaDeFiltros(FormateadorHecho.formatearFiltrosColeccion(buscadores, coleccionResponse.getCriterios())));

                        
                        //List<Hecho> hechos = this.getHechosDeColeccionMetaMapa(coleccionResponse.getCriterios(), buscadores);
                        /*List<HechoRef> hechosRef = new ArrayList<>();
                        for (Hecho h : hechos){
                            h.getAtributosHecho().setFuente(Fuente.PROXY);
                            hechosRef.add(new HechoRef(h.getId(), Fuente.PROXY));
                        }
                        coleccion.setHechos(hechosRef);
                        colecciones.add(coleccion);*/
                    }
                    return colecciones;
                });
    }

    public List<Hecho> getHechosMetaMapa(String url_1, FiltroHechosDTO filtros, BuscadoresRegistry buscadores){

        try {
            String urlStr = url_1 + "/get";
            URL url = new URL(urlStr);
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestMethod("GET");

            conexion.setRequestProperty("Content-Type", "application/json");

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("categoria", filtros.getCriterios().getCategoriaId());
            jsonBody.put("fecha_reporte_desde",filtros.getCriterios().getFechaCargaInicial());
            jsonBody.put("fecha_reporte_hasta", filtros.getCriterios().getFechaCargaFinal());
            jsonBody.put("fecha_acontecimiento_desde", filtros.getCriterios().getFechaAcontecimientoInicial());
            jsonBody.put("fecha_acontecimiento_hasta", filtros.getCriterios().getFechaAcontecimientoFinal());
            jsonBody.put("pais", filtros.getCriterios().getPaisId());
            jsonBody.put("provincia",filtros.getCriterios().getProvinciaId());
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
                    Categoria categoria = buscadores.getBuscadorCategoria().buscar(obj.getString("categoria"));
                    hecho.getAtributosHecho().setCategoria_id(categoria != null ? categoria.getId() : null);
                    UbicacionString ubicacionString = Geocodificador.obtenerUbicacion(obj.getDouble("latitud"),obj.getDouble("longitud"));
                    if(ubicacionString != null) {
                        Pais pais = buscadores.getBuscadorPais().buscar(ubicacionString.getPais());
                        Provincia provincia = buscadores.getBuscadorProvincia().buscar(ubicacionString.getProvincia());
                        Ubicacion ubicacion = buscadores.getBuscadorUbicacion().buscarOCrear(pais, provincia);
                        hecho.getAtributosHecho().setUbicacion_id(ubicacion != null ? ubicacion.getId() : null);
                        hecho.getAtributosHecho().setLatitud(obj.getDouble("latitud"));
                        hecho.getAtributosHecho().setLatitud(obj.getDouble("longitud"));
                    }
                    hecho.getAtributosHecho().setOrigen(Origen.FUENTE_PROXY_METAMAPA);
                    hecho.getAtributosHecho().setFuente(Fuente.PROXY);
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


    public List<Hecho> getHechosDeColeccionMetaMapa(ProxyDTO atributos, String url_1, BuscadoresRegistry buscadores){
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
                    Categoria categoria = buscadores.getBuscadorCategoria().buscar(obj.getString("categoria"));
                    hecho.getAtributosHecho().setCategoria_id(categoria != null ? categoria.getId() : null);
                    UbicacionString ubicacionString = Geocodificador.obtenerUbicacion(obj.getDouble("latitud"),obj.getDouble("longitud"));
                    if(ubicacionString != null) {
                        Pais pais = buscadores.getBuscadorPais().buscar(ubicacionString.getPais());
                        Provincia provincia = buscadores.getBuscadorProvincia().buscar(ubicacionString.getProvincia());
                        Ubicacion ubicacion = buscadores.getBuscadorUbicacion().buscarOCrear(pais, provincia);
                        hecho.getAtributosHecho().setUbicacion_id(ubicacion != null ? ubicacion.getId() : null);
                        hecho.getAtributosHecho().setLatitud(obj.getDouble("latitud"));
                        hecho.getAtributosHecho().setLatitud(obj.getDouble("longitud"));
                    }
                    hecho.getAtributosHecho().setOrigen(Origen.FUENTE_PROXY_METAMAPA);
                    hecho.getAtributosHecho().setFuente(Fuente.PROXY);
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








