package modulos.Front.config;

import jakarta.servlet.http.HttpServletResponse;
import modulos.Front.providers.CustomAuthProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.SecurityFilterChain;


@EnableMethodSecurity(prePostEnabled = true)
@Configuration
public class SecurityConfig {

    // Ahora, estoy diciendole a spring security que haga el login con CustomAuthProvider
    @Bean
    public AuthenticationManager authManager(HttpSecurity http, CustomAuthProvider provider) throws Exception{
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(provider)
                .build();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Autorizaciones básicas
                .authorizeHttpRequests(auth -> auth
                        // Estos recursos son públicos
                        //.requestMatchers("/login", "/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                        .requestMatchers("/","/login/**","/login", "/usuarios/cargar-register", "/usuarios/registrar-usuario", "/public/**").permitAll()
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
                        .defaultSuccessUrl("/", true)
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
