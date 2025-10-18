package modulos.filters;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import modulos.agregacion.entities.DbMain.usuario.Usuario;
import modulos.agregacion.repositories.DbMain.IUsuarioRepository;
import modulos.shared.utils.JwtUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private IUsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // Intenta obtener en el header el authorization bearer (el token)
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                Claims claims = JwtUtil.parseClaims(token);
                String username = claims.getSubject();
                //String rol = claims.get("rol", String.class);
                Usuario usuario = usuarioRepository.findByNombreDeUsuario(username).orElse(null);

                // Si o si va a ser distinto de null porque ya se chequea antes pero igual pongo el if
                if (usuario != null){

                    var auth = new UsernamePasswordAuthenticationToken(
                            username, null
                    );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }


            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido");
                return;
            }
        } else {
            System.out.println("No hay token de autorización");
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/usuario/public/")
                || path.startsWith("/api/usuario/auth")
                || path.startsWith("/public/");
    }

}