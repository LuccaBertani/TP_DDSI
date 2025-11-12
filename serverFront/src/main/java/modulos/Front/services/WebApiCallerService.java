package modulos.Front.services;

import jakarta.servlet.http.HttpServletRequest;
import modulos.Front.dtos.input.AuthResponseDTO;
import modulos.Front.dtos.input.ImportacionHechosInputDTO;
import modulos.Front.dtos.input.TokenResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.netty.http.client.HttpClient;

import java.util.List;

@Service
public class WebApiCallerService {

    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8080")
            .clientConnector(new ReactorClientHttpConnector(
                    HttpClient.create().followRedirect(false)
            ))
            .build();


    // Method de ezequiel
    public <T> ResponseEntity<T> executeWithTokenRetry(
            java.util.function.Function<String, reactor.core.publisher.Mono<ResponseEntity<T>>> apiCall) {


        String accessToken = getAccessTokenFromSession();
        System.out.println("ACCESS TOKEN DE x: " + accessToken);
        String refreshToken = getRefreshTokenFromSession();

        System.out.println("REFRESH TOKEN DE x: " + refreshToken);

        TokenResponse tr = TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        if (accessToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            // 1) Primer intento con el access token actual
            return apiCall.apply(accessToken).block();

        } catch (WebClientResponseException e) {
            // 2) Si expiró (401/403) y tengo refresh → intento refrescar y reintentar
            if ((e.getStatusCode() == HttpStatus.UNAUTHORIZED || e.getStatusCode() == HttpStatus.FORBIDDEN)
                    && refreshToken != null) {
                try {
                    AuthResponseDTO newTokens = refreshToken(tr);

                    if (newTokens == null || newTokens.getAccessToken() == null) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                    }

                    // Guardar nuevos tokens en sesión
                    ServletRequestAttributes attrs =
                            (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
                    HttpServletRequest request = attrs.getRequest();
                    request.getSession().setAttribute("accessToken", newTokens.getAccessToken());
                    if (newTokens.getRefreshToken() != null) {
                        request.getSession().setAttribute("refreshToken", newTokens.getRefreshToken());
                    }

                    // 3) Reintento con el nuevo access token
                    try {
                        return apiCall.apply(newTokens.getAccessToken()).block();
                    } catch (WebClientResponseException e2) {
                        return ResponseEntity.status(e2.getStatusCode()).build();
                    } catch (Exception ex2) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    }
                } catch (Exception refreshError) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                }
            }

            // 4) Otros códigos HTTP → devuelvo el status correspondiente
            return ResponseEntity.status(e.getStatusCode()).build();

        } catch (Exception e) {
            // 5) Errores no HTTP (timeout, conexión, etc.)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Refresca el access token usando el refresh token
     */
    private AuthResponseDTO refreshToken(TokenResponse tokenResponse) {
        try {


            AuthResponseDTO response = webClient
                    .post()
                    .uri("/auth/refresh")
                    .bodyValue(tokenResponse)
                    .retrieve()
                    .bodyToMono(AuthResponseDTO.class)
                    .block();

            // Actualizar tokens en sesión
            updateTokensInSession(response.getAccessToken(), response.getRefreshToken());
            return response;

        } catch (Exception e) {
            throw new RuntimeException("Error al refrescar token: " + e.getMessage(), e);
        }
    }


    /**
     * Actualiza los tokens en la sesión HTTP actual.
     */
    private void updateTokensInSession(String newAccessToken, String newRefreshToken) {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        if (newAccessToken != null) {
            request.getSession().setAttribute("accessToken", newAccessToken);
        }

        if (newRefreshToken != null) {
            request.getSession().setAttribute("refreshToken", newRefreshToken);
        }

    }



    /**
     * Obtiene el access token de la sesión
     */
    private String getAccessTokenFromSession() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        return (String) request.getSession().getAttribute("accessToken");
    }

    public String getUsernameFromSession() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        return (String) request.getSession().getAttribute("username");
    }



    /**
     * Obtiene el refresh token de la sesión
     */
    private String getRefreshTokenFromSession() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        return (String) request.getSession().getAttribute("refreshToken");
    }

    /**
     * Ejecuta una llamada HTTP GET que retorna una lista
     */
    // Method de ezequiel
    /*public <T> List<T> getList(String url, Class<T> responseType) {
        return executeWithTokenRetry(accessToken ->
                webClient
                        .get()
                        .uri(url)
                        .header("Authorization", "Bearer " + accessToken)
                        .retrieve()
                        .bodyToFlux(responseType)
                        .collectList()
                        .block()
        );
    }*/

    public <T> ResponseEntity<List<T>> getList(String url, Class<T> elementType) {
        return executeWithTokenRetry(token ->
                webClient.get()
                        .uri(url)
                        .header("Authorization", "Bearer " + token)
                        .retrieve()
                        .toEntityList(elementType) // Mono<ResponseEntity<List<T>>>
        );
    }

    public <T> ResponseEntity<List<T>> getListTokenOpcional(String url, Class<T> elementType) {
        if (getUsernameFromSession()==null) {
            return webClient.get()
                    .uri(url)
                    .retrieve()
                    .toEntityList(elementType)
                    .block()
                    ;
        }
        else{
            return getList(url, elementType);
        }
    }

    public <T> ResponseEntity<List<T>> postListTokenOpcional(String url, Object body, Class<T> elementType) {
        if (getUsernameFromSession()==null) {
            return webClient.post()
                    .uri(url)
                    .bodyValue(body)
                    .retrieve()
                    .toEntityList(elementType)
                    .block()
                    ;
        }
        else{
            return postList(url, body, elementType);
        }
    }

    public <T> ResponseEntity<T> getEntity(String url, Class<T> elementType){
        return executeWithTokenRetry(token ->
                webClient.get()
                        .uri(url)
                        .header("Authorization", "Bearer " + token)
                        .retrieve()
                        .toEntity(elementType)
        );
    }

    public <T> ResponseEntity<T> getEntityTokenOpcional(String url, Class<T> elementType){

        if (getUsernameFromSession()==null) {
            return
                    webClient.get()
                            .uri(url)
                            .retrieve()
                            .toEntity(elementType)
                            .block();
        }
        else{
            return getEntity(url, elementType);
        }
    }



    public <T> ResponseEntity<T> postEntity(String url, Object body, Class<T> elementType){
        return executeWithTokenRetry(token ->
                webClient.post()
                        .uri(url)
                        .header("Authorization", "Bearer " + token)
                        .bodyValue(body)
                        .retrieve()
                        .toEntity(elementType)
        );
    }

    public <T> ResponseEntity<T> postEntityTokenOpcional(String url, Object body, Class<T> elementType){
        if (getUsernameFromSession()==null) {
            return webClient
                    .post()
                    .uri(url)
                    .bodyValue(body)
                    .retrieve()
                    .toEntity(elementType)
                    .block();
        }
        else{
            return postEntity(url,body,elementType);
        }

    }


    public <T> ResponseEntity<T> postEntity(String url, Class<T> elementType){
        return executeWithTokenRetry(token ->
                webClient.post()
                        .uri(url)
                        .header("Authorization", "Bearer " + token)
                        .retrieve()
                        .toEntity(elementType)
        );
    }

    public <T> ResponseEntity<List<T>> postList(String url, Object body, Class<T> elementType){
        return executeWithTokenRetry(token ->
                webClient.post()
                        .uri(url)
                        .header("Authorization", "Bearer " + token)
                        .bodyValue(body)
                        .retrieve()
                        .toEntityList(elementType)
        );
    }



    public <T> ResponseEntity<T> login(Object body, Class<T> elementType) {
        try {
            return webClient
                .post()
                .uri( "/api/usuario/auth")
                .bodyValue(body)
                .retrieve()
                .toEntity(elementType)
                .block();


        } catch (WebClientResponseException e) {

            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                // Login fallido - credenciales incorrectas
                return null;
            }
            // Otros errores HTTP
            throw new RuntimeException("Error en el servicio de autenticación: " + e.getMessage(), e);

        } catch (Exception e) {
            throw new RuntimeException("Error de conexión con el servicio de autenticación: " + e.getMessage(), e);
        }
    }

    public ResponseEntity<Void> importarHecho(MultipartFile file, ImportacionHechosInputDTO dto){
        MultipartBodyBuilder builder = new MultipartBodyBuilder();

        builder.part("meta", dto).contentType(MediaType.APPLICATION_JSON);
        builder.part("file", file.getResource())
                .header("Content-Disposition", "form-data; name=\"file\"; filename=\"" + file.getOriginalFilename() + "\"");

        return executeWithTokenRetry(token-> webClient.post()
                .uri("/api/hechos/importar")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .toEntity(Void.class)
        );


    }





}
