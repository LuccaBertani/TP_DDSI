package modulos.Front.services;

import jakarta.servlet.http.HttpServletRequest;
import modulos.Front.ApiCall;
import modulos.Front.dtos.output.AuthResponseDTO;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Service
public class WebApiCallerService {

    private final WebClient webClient = WebClient.create("http://localhost:8083");



    // Method de ezequiel
    /*public <T> T executeWithTokenRetry(ApiCall<T> apiCall) {
        String accessToken = getAccessTokenFromSession();
        String refreshToken = getRefreshTokenFromSession();

        if (accessToken == null) {
            throw new RuntimeException("No hay token de acceso disponible");
        }

        try {
            // Primer intento con el token actual
            return apiCall.execute(accessToken);
        } catch (WebClientResponseException e) {
            if ((e.getStatusCode() == HttpStatus.UNAUTHORIZED || e.getStatusCode() == HttpStatus.FORBIDDEN)
                    && refreshToken != null) {
                try {
                    // Token expirado, intentar refresh
                    AuthResponseDTO newTokens = refreshToken(refreshToken);

                    // Segundo intento con el nuevo token
                    return apiCall.execute(newTokens.getAccessToken());
                } catch (Exception refreshError) {
                    throw new RuntimeException("Error al refrescar token y reintentar: " + refreshError.getMessage(),
                            refreshError);
                }
            }

            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new RuntimeException("SOY UN ESTORBO");
            }

            throw new RuntimeException("Error en llamada al API: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al ejecutar la llamada al API", e);
        }
    }*/

    public <T> ResponseEntity<T> executeWithTokenRetry(
            java.util.function.Function<String, reactor.core.publisher.Mono<ResponseEntity<T>>> apiCall) {

        String accessToken = getAccessTokenFromSession();
        String refreshToken = getRefreshTokenFromSession();

        if (accessToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return apiCall.apply(accessToken)
                .onErrorResume(WebClientResponseException.class, e -> {
                    // Si el error es UNAUTHORIZED o FORBIDDEN, intento refrescar
                    if ((e.getStatusCode() == HttpStatus.UNAUTHORIZED || e.getStatusCode() == HttpStatus.FORBIDDEN)
                            && refreshToken != null) {
                        return Mono.defer(() -> {
                            AuthResponseDTO newTokens = refreshToken(refreshToken);
                            return apiCall.apply(newTokens.getAccessToken());
                        });
                    }

                    // Si es NOT_FOUND, devuelvo un 404 explícito
                    if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
                    }

                    // Cualquier otro error: response 500 con mensaje
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(null));
                })
                .onErrorResume(Throwable.class, ex ->
                        Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()))
                .block(); // mantiene comportamiento bloqueante final
    }


    private AuthResponseDTO refreshToken(String refreshToken) {
        return webClient.post()
                .uri("/api/auth/refresh") // endpoint del backend
                .bodyValue(Collections.singletonMap("refreshToken", refreshToken))
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        // Éxito → deserializa normalmente
                        return response.bodyToMono(AuthResponseDTO.class);
                    } else {
                        // Cualquier otro status (401, 403, 500, etc.) → devolvemos vacío
                        return Mono.empty();
                    }
                })
                .blockOptional() // devuelve Optional<AuthResponseDTO>
                .orElse(null);   // si vacío → null
    }



    /**
     * Obtiene el access token de la sesión
     */
    private String getAccessTokenFromSession() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        return (String) request.getSession().getAttribute("accessToken");
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


}
