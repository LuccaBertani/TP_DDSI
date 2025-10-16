package modulos.config;

import lombok.AllArgsConstructor;
import modulos.agregacion.repositories.DbMain.IUsuarioRepository;
import modulos.filters.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    private IUsuarioRepository usuarioRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("=== CONFIGURANDO SECURITY ===");

        http
                .csrf(AbstractHttpConfigurer::disable)
                // Establezco a mi server stateless. No maneja sesiones. Se maneja con tokens
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/api/auth", "/api/auth/refresh", "/api/public").permitAll();
                    auth.anyRequest().authenticated();
                })
                // Todas las request que no sean de rutas públicas, pasa por este middleware (el filtro personalizado nuestro)
                .addFilterBefore(new JwtAuthenticationFilter(usuarioRepository), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

