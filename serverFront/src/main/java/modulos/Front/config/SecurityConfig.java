package modulos.Front.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;


@EnableMethodSecurity(prePostEnabled = true)
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Autorizaciones básicas
                .authorizeHttpRequests(auth -> auth
                        // Estos recursos son públicos
                        .requestMatchers("/login", "/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                        //.requestMatchers("/alumnos/**").hasAnyRole("ADMIN", "DOCENTE")
                        // Los chequeos en server front que se quieran agregar se agregan con requestMatchers
                        // igualmente los chequeos los estamos haciendo en los controllers
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        // indico a donde se encuentra el login
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        // Una vez el login sea exitoso, lo redirijo a...
                        // (despues ver a donde redirigir)
                        .defaultSuccessUrl("/index", true)
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        // el logout success
                        .logoutSuccessUrl("/login?logout") // redirigir tras logout
                        .permitAll()
                )
                .exceptionHandling(ex -> ex
                        // usuario no autenticado -> redirigir a login
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendRedirect("/login?unauthorized"))
                        // usuario autenticado pero sin permisos -> redirigir a pagina de error
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                response.sendRedirect("/403"))
                )
                .sessionManagement(session -> session
                        .sessionFixation().migrateSession()
                );

        return http.build();
    }

}
